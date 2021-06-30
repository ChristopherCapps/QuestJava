package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Emoji;
import com.quest.io.Message;
import com.quest.model.Crew;
import com.quest.model.Explorer;
import com.quest.model.Race;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class JoinCommand extends Command.ArgumentCommand {
    private static final String NAME = "join";
    private static final String HELP_TEXT = "Joins a crew for the race; for example, type 'join 1' to join the first " +
            "crew in the list. To see a list, type 'crews'.";
    private static final String[] KEYWORDS = { "join" };

    public JoinCommand() {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
    }

    private Integer parseInt(final String str) {
        try {
            return Integer.parseInt(str);
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    protected Optional<Message> evaluatePreconditions(Engine engine, Race race, IOService ioService, Explorer explorer, Optional<String> arguments) {
        return super.evaluatePreconditions(engine, race, ioService, explorer, arguments)
                .or(() -> (!explorer.isGameMaster() && // allow game master always
                        (race.getCrew(explorer).isPresent() && race.getState() != Race.State.NOT_STARTED)) ? // allow explorer who doesn't have a crew, even after the race starts, but not others who have joined
                        respondWith(Message.newBuilder().append("You can't change crews once the race has begun!").build()) :
                        Optional.empty());
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, final Race race, final IOService ioService, final Explorer explorer,
                                                  final String[] arguments) {
        return respondWith(
                Optional.ofNullable(arguments[0]) // Shouldn't be null
                        .map(this::parseInt)
                        .flatMap(race::crewById)
                        .map(crew -> {
                            final Optional<Crew> currentCrewAssignment = race.getCrew(explorer);
                            race.addExplorer(explorer, crew);
                            return Message.newBuilder().append(
                                    currentCrewAssignment.map(value -> value.equals(crew) ?
                                            "Looks like you're already on the " + crew.name() + "crew!" :
                                            "Ok, I've now switched you over to the " + crew.name() + " crew. "
                                                    + Emoji.HIGH_FIVE)
                                            .orElseGet(() -> "Great! I've added you to the " + crew.name() + " crew. " +
                                                    Emoji.HIGH_FIVE + " Enjoy the race! " + Emoji.RUNNER +
                                                    "\n\nA couple of things to note:\n" +
                                                    "1. You can change the name of your crew at any time. Just use the " +
                                                    "RENAME CREW command; for example, type 'rename crew Bots'\n" +
                                                    "2. There are lots of other commands. To see them, type COMMANDS or ?.")).build();
                        })
                        .orElse(Message.newBuilder().append("It looks like you're trying to pick a crew, but I didn't " +
                                        "understand which one you meant by '%s'. You might type '%s' to see the list again. %s",
                                arguments[0], CrewCommand.NAME, Emoji.QUESTIONER).build()));
    }
}
