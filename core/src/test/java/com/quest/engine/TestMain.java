package com.quest.engine;

import com.quest.baldydash.BaldyDash2021;
import com.quest.engine.command.*;
import com.quest.engine.command.privileged.*;
import com.quest.io.Message;
import com.quest.io.TestIOService;
import com.quest.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestMain {

    static final Logger logger = LoggerFactory.getLogger(TestMain.class);

    public static final String EXPLORER_ID = "9193620900";
    public static final String GAMEMASTER_ID = "9198892572";

    public static final String EXPLORER_1_ID = "1";
    public static final String EXPLORER_2_ID = "2";
    public static final String EXPLORER_3_ID = "3";
    public static final String EXPLORER_4_ID = "4";

    public static final String[] EXPLORERS = { EXPLORER_1_ID, EXPLORER_2_ID, EXPLORER_3_ID, EXPLORER_4_ID };
    public static final String[] NAMES = { "Chris", "Molly", "Ethan", "Benton" };

    private Engine engine;
    private CommandRegistry registry;
    private IOService ioService;

    @BeforeEach
    public void beforeEach() {

        final var baldyDash = BaldyDash2021.BALDY_DASH_QUEST;

        List<Command> commands = new LinkedList<>();
        commands.add(new RestartCommand());
        commands.add(new StartCommand());
        commands.add(new StatusCommand());
        commands.add(new ClueCommand());
        commands.add(new RemoveCommand());
        commands.add(new CrewCommand());
        commands.add(new RenameCrewCommand());
        commands.add(new GameMasterCommand());
        commands.add(new JoinCommand());
        commands.add(new NameCommand());
        commands.add(new TauntCommand());
        commands.add(new CourseCommand());
        commands.add(new HintCommand());
        commands.add(new SetWaypointCommand());
        registry = new CommandRegistry(commands);
        new HelpCommand(registry);

        ioService = new TestIOService();
        engine = new Engine(registry, baldyDash, ioService, new AnswerCommand());
    }

    private Message sendEngineMessage(final String channel, final String message) {
        final Message response = engine.onMessageReceived(channel, message).get();
        //if (response.photoUrl() != null) System.out.println("[" + response.photoUrl() + "]");
        return response;
    }

    @Test
    public void testQuest1() {
        // join players to crews
        Arrays.stream(EXPLORERS).forEach(this::testGreeting);
        assertTrue(sendEngineMessage(EXPLORER_1_ID, "crew").text().contains("Here are the crews"));
        assertTrue(sendEngineMessage(EXPLORER_3_ID, "crew").text().contains("Here are the crews"));
        testJoin(EXPLORER_1_ID, 1);
        testJoin(EXPLORER_2_ID, 1);
        testJoin(EXPLORER_3_ID, 2);
        testJoin(EXPLORER_4_ID, 2);
        // Create array of Explorer objects
        final Explorer[] explorers = new Explorer[EXPLORERS.length];
        IntStream.range(0, EXPLORERS.length).forEach(i -> explorers[i] = engine.race().getExplorer(EXPLORERS[i]).get());
        assertTrue(sendEngineMessage(EXPLORER_2_ID, "garbage").text().contains("not sure"));
        // Allow help
        Arrays.stream(EXPLORERS).forEach(explorerId -> assertTrue(sendEngineMessage(explorerId, "help").text().contains("list of commands")));
        // Allow name command
        IntStream.range(0, EXPLORERS.length).forEach(i ->
                assertTrue(sendEngineMessage(EXPLORERS[i], "name " + NAMES[i]).text().contains(NAMES[i])
                        && engine.race().getExplorer(EXPLORERS[i]).get().name().equals(NAMES[i])));
        // Confirm state of crews
        assertTrue(engine.race().crewById(1).map(crew -> engine.race().getExplorers(crew).size()).orElseThrow(IllegalStateException::new) == 2);
        assertTrue(sendEngineMessage(EXPLORER_2_ID, "garbage").text().contains("not sure"));
        assertTrue(engine.race().crewById(2).map(crew -> engine.race().getExplorers(crew).size()).orElseThrow(IllegalStateException::new) == 2);
        // join GM
        testGameMasterValidBeforeJoined();
        // try to list clue: none
        Arrays.stream(EXPLORERS).forEach(explorerId -> assertTrue(sendEngineMessage(explorerId, "clue").text().contains("won't have a waypoint clue")));
        Arrays.stream(EXPLORERS).forEach(explorerId -> assertTrue(sendEngineMessage(explorerId, "waypoint").text().contains("won't have a waypoint clue")));
        // Rename crews
        final Crew crew1 = engine.race().getCrew(explorers[0]).get();
        final Crew crew2 = engine.race().getCrew(explorers[2]).get();
        assertTrue(sendEngineMessage(EXPLORER_1_ID, "rename crew Frogs").text().contains("Frogs") && crew1.name().equals("Frogs"));
        assertTrue(sendEngineMessage(EXPLORER_4_ID, "rename crew Crows").text().contains("Crows") && crew2.name().equals("Crows"));
        assertTrue(sendEngineMessage(EXPLORER_2_ID, "garbage").text().contains("not sure"));
        // GM status
        sendEngineMessage(GAMEMASTER_ID, "status");
        // Switch teams before race and change crew name, then switch back
        assertTrue(sendEngineMessage(EXPLORER_1_ID, "join 2").text().contains("switched") && engine.race().getCrew(explorers[0]).get() == crew2);
        assertTrue(sendEngineMessage(EXPLORER_1_ID, "rename crew Dufus").text().contains("Dufus") && crew2.name().equals("Dufus"));
        assertTrue(sendEngineMessage(EXPLORER_1_ID, "join 1").text().contains("switched") && engine.race().getCrew(explorers[0]).get() == crew1);
        // Start race!
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "start").text().contains("been started") && engine.race().getState() == Race.State.IN_PROGRESS);
        // Try to change crews but not allowed
        assertTrue(sendEngineMessage(EXPLORER_1_ID, "join 2").text().contains("can't change"));
        // Send a taunt
        assertTrue(sendEngineMessage(EXPLORER_2_ID, "taunt Try to keep up!").text().contains("taunt was sent"));
        // Explorer 2 leaves and comes back
        assertTrue(sendEngineMessage(EXPLORER_2_ID, "remove").text().contains("Cya"));
        testGreeting(EXPLORER_2_ID);
        testJoin(EXPLORER_2_ID, 1);
        engine.race().crews().forEach(crew -> {
                Optional<Waypoint> waypoint = engine.race().getWaypoint(crew);
                final Explorer explorer = engine.race().getExplorers(crew).stream().findFirst().get();
                while (waypoint.isPresent()) {
                    sendEngineMessage(explorer.uid(), "wrong");
                    //assertTrue(waypoint.get().equals(engine.race().getWaypoint(crew).get()));
                    sendEngineMessage(explorer.uid(), Arrays.stream(waypoint.get().lock().keys()).findFirst().get().values()[0]);
                    final Optional<Waypoint> newWaypoint = engine.race().getWaypoint(crew);
                    if (newWaypoint.isPresent()) {
                        assertTrue(!waypoint.get().equals(newWaypoint.get()));
                    }
                    waypoint = newWaypoint;
                    // Issue GM status command
                    sendEngineMessage(GAMEMASTER_ID, "status");
                }
        });
    }


    @Test
    public void testGreeting() {
        testGreeting(EXPLORER_ID);
    }

    public void testGreeting(final String explorerId) {
        assertTrue(sendEngineMessage(explorerId, "hi").text().contains("Hello"));
    }

    @Test
    public void testInvalidJoin() {
        testGreeting();
        assertTrue(sendEngineMessage(EXPLORER_ID, "join").text().contains("trying to use the JOIN"));
        assertTrue(sendEngineMessage(EXPLORER_ID, "join 3").text().contains("trying to pick"));
    }

    @Test
    public void testListCrews() {
        testGreeting();
        assertTrue(sendEngineMessage(EXPLORER_ID, "crew").text().contains("Here are the crews"));
    }

    protected void testJoin(final String explorerId, final int crewId) {
        assertTrue(sendEngineMessage(explorerId, "join " + crewId).text().contains("added you to"));
        assertTrue(engine.race().getCrew(engine.race().getExplorer(explorerId).get()).get().uid() == crewId);
    }

    @Test
    public void testValidJoin() {
        testListCrews();
        assertTrue(sendEngineMessage(EXPLORER_ID, "join 1").text().contains("added you to"));
        assertTrue(engine.race().getCrew(engine.race().getExplorer(EXPLORER_ID).get()).get().uid() == 1);
        assertTrue(sendEngineMessage(EXPLORER_ID, "join 2").text().contains("switched you"));
        assertTrue(engine.race().getCrew(engine.race().getExplorer(EXPLORER_ID).get()).get().uid() == 2);
    }

    @Test
    public void testCommandOtherThanJoinOrCrewBeforeJoined() {
        testGreeting();
        assertTrue(sendEngineMessage(EXPLORER_ID, "clue").text().contains("first need to be a member"));
        assertTrue(sendEngineMessage(EXPLORER_ID, "taunt").text().contains("first need to be a member"));
    }

    @Test
    public void testNameBeforeJoined() {
        testGreeting();
        assertTrue(sendEngineMessage(EXPLORER_ID, "name Chris").text().contains("nice to meet you"));
    }

    @Test
    public void testGameMasterValidBeforeJoined() {
        testGreeting(GAMEMASTER_ID);
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "gm 1973").text().contains("welcome GameMaster"));
        assertTrue(engine.race().getExplorer(GAMEMASTER_ID).get().isGameMaster());
    }

    @Test
    public void testCourseCommand() {
        testGameMasterValidBeforeJoined();
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "course 1").text().contains("Waypoints"));
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "course 2").text().contains("crew 2"));
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "course 3").text().contains("Unable to locate a crew"));
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "course 1 2").text().contains("crew 2"));
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "course 1 a").text().contains("Processing stopped"));
    }

    @Test
    public void testSetWaypointCommand() {
        testGameMasterValidBeforeJoined();
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "setw 1 1").text().contains("Survivor"));
        assertTrue(engine.race().getCourse(engine.race().crewById(1).get()).get().waypointIndex(
                engine.race().getWaypoint(engine.race().crewById(1).get()).get()) == 1);
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "setw 1 3").text().contains("Thistle"));
        assertTrue(engine.race().getCourse(engine.race().crewById(1).get()).get().waypointIndex(
                engine.race().getWaypoint(engine.race().crewById(1).get()).get()) == 3);
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "setw 1").text().contains("must specify"));
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "setw a b").text().contains("must be numbers"));
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "setw 0 2").text().contains("Unable to locate a crew"));
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "setw 1 0").text().contains("does not exist"));
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "setw 2 1").text().contains("Survivor"));
        assertTrue(engine.race().getCourse(engine.race().crewById(2).get()).get().waypointIndex(
                engine.race().getWaypoint(engine.race().crewById(2).get()).get()) == 1);
    }

    @Test
    public void testCantUseHintCommandBeforeJoin() {
        testGreeting(EXPLORER_ID);
        assertTrue(sendEngineMessage(EXPLORER_ID, "hint").text().contains("doesn't appear"));
    }

    @Test
    public void testCanUseHintCommandAfterJoin() {
        testValidJoin();
        testGameMasterValidBeforeJoined();
        assertTrue(sendEngineMessage(EXPLORER_ID, "hint").text().contains("has been sent"));
    }

    @Test
    public void testGameMasterInvalidBeforeJoined() {
        testGreeting(GAMEMASTER_ID);
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "gm 1973x").text().contains("no soup"));
        assertFalse(engine.race().getExplorer(GAMEMASTER_ID).get().isGameMaster());
    }

    @Test
    public void testStartCommandWhenNotStarted() {
        testGameMasterValidBeforeJoined();
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "start").text().contains("has been started"));
        assertTrue(engine.race().getState() == Race.State.IN_PROGRESS);
    }

    @Test
    public void testStartCommandWhenInProgress() {
        testGameMasterValidBeforeJoined();
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "start").text().contains("has been started"));
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "start").text().contains("already been started"));
        assertTrue(engine.race().getState() == Race.State.IN_PROGRESS);
    }

    private void startRace() {
        sendEngineMessage(GAMEMASTER_ID, "Hi"); // become GM
        sendEngineMessage(GAMEMASTER_ID, "gm 1973"); // become GM
        sendEngineMessage(GAMEMASTER_ID, "start"); // start race
    }

    @Test
    public void testHelpCommandAnytime() {
        testGreeting();
        assertTrue(sendEngineMessage(EXPLORER_ID, "help").text().contains("some commands"));
        sendEngineMessage(EXPLORER_ID, "join 1"); // join crew
        assertTrue(sendEngineMessage(EXPLORER_ID, "help").text().contains("some commands"));
        startRace();
        assertTrue(sendEngineMessage(EXPLORER_ID, "help").text().contains("some commands"));
    }

    @Test
    public void testCantChangeCrewsAfterRaceStarted() {
        testValidJoin();
        startRace();
        assertTrue(sendEngineMessage(EXPLORER_ID, "join 2").text().contains("can't change crews"));
    }

    @Test
    public void testNewExplorersCanJoinAfterRaceStarted() {
        startRace();
        testGreeting();
        assertTrue(sendEngineMessage(EXPLORER_ID, "crew").text().contains("Here are"));
        assertTrue(sendEngineMessage(EXPLORER_ID, "join 1").text().contains("added you to"));
    }

    @Test
    public void testRemoveCommand() {
        testGreeting(EXPLORER_ID);
        assertTrue(sendEngineMessage(EXPLORER_ID, "remove").text().contains("Cya"));
        assertTrue(engine.race().getExplorer(EXPLORER_1_ID).isEmpty());
    }

    @Test
    public void testRenameCrewCommandBeforeJoined() {
        testGreeting();
        assertTrue(sendEngineMessage(EXPLORER_ID, "rename crew test").text().contains("first need to be a member"));
    }

    @Test
    public void testRenameCrewCommandAfterJoined() {
        testValidJoin();
        assertTrue(sendEngineMessage(EXPLORER_ID, "rename crew Beatles").text().contains("I've renamed your crew"));
        assertTrue(engine.race().getCrew(engine.race().getExplorer(EXPLORER_ID).get()).get().name().equals("Beatles"));
    }

    @Test
    public void testTauntBeforeJoined() {
        testGreeting();
        assertTrue(sendEngineMessage(EXPLORER_ID, "taunt You stink!").text().contains("first need to be"));
    }

    @Test
    public void testTauntAfterJoined() {
        testValidJoin();
        assertTrue(sendEngineMessage(EXPLORER_ID, "taunt You stink!").text().contains("taunt was sent"));
    }

    @Test
    public void testStatusCommand() {
        testGreeting();
        startRace();
        assertTrue(sendEngineMessage(GAMEMASTER_ID, "status").text().contains("Waypoint"));
    }


//    @Test
//    public void testCommandBeforeCrewSelection() {
//        testGreeting();
//        assertTrue(sendEngineMessage(EXPLORER_ID, "crewname Bearcats").text().contains("I need you to pick"));
//    }
//
//    @Test
//    public void testCrewNameChange() {
//        testGreeting();
//        sendEngineMessage(EXPLORER_ID, "crew 1");
//        sendEngineMessage(EXPLORER_ID, "crewname Bearcats");
//        assertTrue(engine.race().getCrew(engine.race().getExplorer(EXPLORER_ID).get()).get().name().equals("Bearcats"));
//    }
//
//    @Test
//    public void testInvalidCrewNameChange() {
//        testGreeting();
//        sendEngineMessage(EXPLORER_ID, "crew 1");
//        assertTrue(sendEngineMessage(EXPLORER_ID, "crewname   ").text().contains("What's that?"));
//    }
//
//    @Test
//    public void testGameMasterMode() {
//        sendEngineMessage(GAMEMASTER_ID, "hello");
//        assertTrue(sendEngineMessage(GAMEMASTER_ID, "gamemaster 1234").text().contains("game master"));
//        assertTrue(engine.race().getExplorer(GAMEMASTER_ID).get().isGameMaster());
//        sendEngineMessage(EXPLORER_ID, "hello");
//        sendEngineMessage(EXPLORER_ID, "crew 1");
//    }
//
//    @Test
//    public void testNonGameMasterCannotUsePrivilegedCommand() {
//        testValidCrewSelection();
//        assertTrue(sendEngineMessage(EXPLORER_ID, "start").text().contains("Not sure"));
//    }
//
//    @Test
//    public void testGameMasterCanUsePrivilegedCommand() {
//        testGameMasterMode();
//        assertTrue(sendEngineMessage(GAMEMASTER_ID, "start").text().contains("started"));
//    }
//
//    @Test
//    public void testExplorerNameChangeCommand() {
//        testGreeting();
//        sendEngineMessage(EXPLORER_ID, "name ralph");
//        assertTrue(engine.race().getExplorer(EXPLORER_ID).get().name().equals("Ralph"));
//    }
//
//    @Test
//    public void testHelpCommand() {
//        testGreeting();
//        assertTrue(sendEngineMessage(EXPLORER_ID, "help").text().contains("some commands"));
//    }
//
//    @Test
//    public void testClueCommandBeforeStart() {
//        testGreeting();
//        sendEngineMessage(EXPLORER_ID, "crew 1");
//        assertTrue(sendEngineMessage(EXPLORER_ID, "clue").text().contains("can't show you"));
//    }
}