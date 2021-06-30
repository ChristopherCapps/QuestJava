package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Message;
import com.quest.model.Explorer;
import com.quest.model.Race;
import com.quest.model.Waypoint;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ClueCommand extends Command.NoArgumentCommand {
    static final String NAME = "clue";
    static final String HELP_TEXT = "Repeats the clue for your current waypoint; for example, type 'clue'";
    static final String[] KEYWORDS = { "clue", "waypoint" };

    public ClueCommand() {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> evaluatePreconditions(Engine engine, Race race, IOService ioService, Explorer explorer, Optional<String> arguments) {
        return super.evaluatePreconditions(engine, race, ioService, explorer, arguments)
                .or(() -> evaluateExplorerHasACrewPrecondition(engine, race, ioService, explorer, arguments))
                .or(() -> {
                    if (race.getState() != Race.State.IN_PROGRESS) {
                        return respondWith(Message.newBuilder().append("Your crew won't have a waypoint clue until the " +
                                "race starts.").build());
                    } else {
                        return Optional.empty();
                    }
                });
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, final Race race, final IOService ioService, final Explorer explorer) {
        return respondWith(race.getCrew(explorer)
                .flatMap(crew -> race.getCourse(crew)
                        .flatMap(course -> race.getWaypoint(crew)
                                .map(waypoint -> buildWaypointMessage(course, waypoint))))
                .orElse(Message.newBuilder().append("Your crew isn't at a waypoint right now.").build()));
    }

}
