package com.quest.engine;

import com.quest.engine.command.Command;
import com.quest.engine.command.CommandRegistry;
import com.quest.engine.command.HelpCommand;
import com.quest.io.Emoji;
import com.quest.io.Message;
import com.quest.model.*;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;

public class Engine {

    static final Logger logger = LoggerFactory.getLogger(Engine.class);

//    final static String[] RANDOM_CREW_NAMES = { "Wolfpack", "Tarheels", "Beach Bums", "Sandy Toes", "Turtle Watchers",
//            "Sharks", "Jellyfish" };

    final static String[] RANDOM_CREW_NAMES = { "Boys", "Girls" };

    final static Message UNKNOWN_INPUT_MESSAGE = Message.newBuilder().append(
            "Sorry, but I'm not sure what you mean by that. Type COMMANDS or ? to see a list of commands.")
            .build();

    private final CommandRegistry commands;
    private final Quest quest;
    private Race race;
    private final IOService ioService;
    private final Command waypointAnswerCommand;

    public static Engine of(final CommandRegistry commands, final Quest quest, final IOService ioService,
                            final Command waypointAnswerCommand) {
        return new Engine(commands, quest, ioService, waypointAnswerCommand);
    }

    protected Engine(final CommandRegistry commands, final Quest quest, final IOService ioService,
                     final Command waypointAnswerCommand) {
        this.commands = requireNonNull(commands);
        this.quest = requireNonNull(quest);
        this.ioService = new LoggingIoService(requireNonNull(ioService));
        this.waypointAnswerCommand = requireNonNull(waypointAnswerCommand);

        HelpCommand.of(commands);
        this.ioService.setEngine(this);
        initializeRace();
    }

    public Race race() {
        return race;
    }

    public void reset() {
        initializeRace();
    }

    private void initializeRace() {
        logger.debug("Initializing the race: " + quest.name());
        this.race = Race.of(quest);
        this.race.updateStartTime();
        initializeCrews();
    }

    private void initializeCrews() {
        // We need a Crew for each variation of Course
        final Quest.Course[] courses = race.quest().courses().stream().toArray(Quest.Course[]::new);

        int courseLength = -1;
        for (int i = 0; i < courses.length; i++) {
            if (courseLength == -1) {
                courseLength = courses[i].waypoints().size();
            } else if (courseLength != courses[i].waypoints().size()) {
                logger.warn("NOTE! The courses in this quest have a different number of waypoints!");
            }
        }

        IntStream.range(0, courses.length)
                .forEach(i -> {
                    final Crew crew = Crew.of(i+1, pickRandomWithExclusions(
                            new HashSet<>(Arrays.asList(RANDOM_CREW_NAMES)),
                            getCrewNames()));
                    logger.debug("Creating crew: {}}", crew.toString());
                    race.addCrew(crew);
                    race.setCourse(crew, courses[i]);
                    race.setWaypoint(crew, courses[i].firstWaypoint());
                });
    }

    private Set<String> getCrewNames() {
        return race.crews().stream().map(Crew::name).collect(Collectors.toSet());
    }

    private String pickRandomWithExclusions(final Set<String> sourceSet, final Set<String> excludedSet) {
        final Set<String> inclusiveSet = new HashSet<>(sourceSet);
        inclusiveSet.removeAll(excludedSet);
        return inclusiveSet.toArray(new String[]{})[(new Random()).nextInt(inclusiveSet.size())];
    }

    private Optional<Message> respondWith(final Message response) {
        return Optional.of(response);
    }

    private Message generateGreeting() {
        return Message.newBuilder()
                .withUrl("https://stizza-bongo-8997.twil.io/assets/BaldyDash2021.png")
                .append("Hello, there! Looks like you're a new %s explorer. Welcome! %s\n\n",
                        race.quest().name(), Emoji.CHECKERED_FLAG)
                .append(race.getState() == Race.State.NOT_STARTED ?
                        "Good news! The race has not yet started, so now is the perfect time to join a crew." :
                        "A race is already underway, but you can still join a crew!")
                .append(" To do so, use the CREW command to list your choices. Just type 'crew'.").build();
    }

    protected Message onUnknownCommand(final Explorer explorer, final String message) {
        // Treat unknown input as a key to a waypoint clue if the explorer is at a waypoint
        logger.debug("Explorer {} input '{}' does not appear to be a known command", explorer.name(), message);
        return race.getState() == Race.State.IN_PROGRESS ?
                race.getCrew(explorer)
                .flatMap(race::getWaypoint)
                .flatMap(waypoint -> handleWaypointAnswer(explorer, message))
                .orElse(UNKNOWN_INPUT_MESSAGE) :
                UNKNOWN_INPUT_MESSAGE;
    }

    private Optional<Message> handleWaypointAnswer(final Explorer explorer, final String message) {
        logger.debug("Trying to handle {}'s input as an answer.", explorer.name());
        return waypointAnswerCommand.onCommand(this, race, ioService, explorer, message);
    }

    // might need to return optional?
    public synchronized Optional<Message> onMessageReceived(final String channel, final String message) {
        logger.debug("Message received from {}: '{}'", channel, message);
        try {
            final Optional<Message> response = respondWith(
                    race.getExplorer(channel)
                            .map(explorer -> commands.tryMatchCommand(message)
                                    .flatMap(command -> command.onCommand(this, race, ioService, explorer, message))
                                    .orElseGet(() -> onUnknownCommand(explorer, message)))
                            .orElseGet(() -> {
                                logger.debug("Message is from an unknown user and will be treated as a new Explorer");
                                final Explorer newExplorer = Explorer.of(channel);
                                race.addExplorer(newExplorer);
                                return generateGreeting();
                            }));
            logger.info("Responding to explorer {} with {}", channel, response);
            return response;
        } catch (Throwable t) {
            logger.error("An unexpected error occurred which is being suppressed", t);
            return Optional.empty();
        }
    }

    class LoggingIoService implements IOService {
        private IOService delegate;
        LoggingIoService(final IOService ioService) {
            delegate = ioService;
        }
        @Override
        public void setEngine(Engine engine) {
            delegate.setEngine(engine);
        }

        @Override
        public void sendMessage(String channel, Message message) {
            logger.debug("IOService.sendMessage({}, {})", channel, message);
            delegate.sendMessage(channel, message);
        }

        @Override
        public void broadcastMessage(Message message, String... channels) {
            logger.debug("IOService.broadcastMessage({}, {})", message, channels);
            delegate.broadcastMessage(message, channels);
        }
    }
}


/** Wrap the IO service for GameMaster/logging **/