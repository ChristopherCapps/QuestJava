package com.quest.engine.command.privileged;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.engine.command.Command;
import com.quest.io.Message;
import com.quest.model.Explorer;
import com.quest.model.Race;

import java.util.Optional;

public class StartCommand extends Command.NoArgumentCommand {
    static final String NAME = "start";
    static final String HELP_TEXT = "Starts a race!";
    static final String[] KEYWORDS = { "start" };

    public StartCommand() {
        super(NAME, HELP_TEXT, PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> evaluatePreconditions(Engine engine, Race race, IOService ioService, Explorer explorer, Optional<String> arguments) {
        return super.evaluatePreconditions(engine, race, ioService, explorer, arguments)
                .or(() -> race.getState() != Race.State.NOT_STARTED ?
                        respondWith(Message.newBuilder().append("The race has already been started.").build()) :
                        Optional.empty());
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, final Race race, final IOService ioService, final Explorer explorer) {
        return respondWith(startRace(race, ioService));
    }

    private Message startRace(final Race race, final IOService ioService) {
        race.setState(Race.State.IN_PROGRESS);
        race.crews().stream().
                forEach(crew -> race.getCourse(crew)
                        .ifPresentOrElse(
                                course -> race.setWaypoint(crew, course.firstWaypoint()),
                                () -> { throw new IllegalStateException("Crew " + crew.name() + " has no course!"); }));

        broadcastToExplorers(race, ioService, Message.of("It's time to race! Here comes the clue to your first waypoint..."));
        broadcastWaypointClues(race, ioService);
        return Message.newBuilder().append("Done. The race has been started.\n").build();
    }

}
