package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Emoji;
import com.quest.io.Message;
import com.quest.model.Explorer;
import com.quest.model.Race;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class TauntCommand extends Command.ArgumentCommand {
    static final String NAME = "taunt";
    static final String HELP_TEXT = "Sends a message to all the explorers on other crews; " +
        "for example, type 'taunt You're going down!'";
    static final String[] KEYWORDS = { "taunt", "tease" };

    public TauntCommand() {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> evaluatePreconditions(Engine engine, Race race, IOService ioService, Explorer explorer, Optional<String> arguments) {
        return super.evaluatePreconditions(engine, race, ioService, explorer, arguments)
                .or(() -> evaluateExplorerHasACrewPrecondition(engine, race, ioService, explorer, arguments));
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService, Explorer explorer, String[] arguments) {
        final String taunt = StringUtils.join(arguments, " ");
        return race.getCrew(explorer)
                .map(crew -> {
                    race.crews().stream()
                            .filter(c -> !c.equals(crew))
                            .forEach(c -> broadcastToCrew(race, ioService, c, Message.newBuilder().append(
                                    "%s from %s has a taunt for you: \"%s\" %s", explorer.name(),
                                    crew.definiteName(), taunt, Emoji.SMIRKING_FACE).build()));
                    broadcastToCrewExcept(race, ioService, crew, Message.newBuilder().append("Your crewmate, %s, has just sent " +
                            "the following taunt to the other crews: `%s` %s", explorer.name(), taunt, Emoji.CLAPPING).build(), explorer);
                    return respondWith(Message.newBuilder().append("Your taunt was sent to all other crews, and your " +
                            "own crew was notified %s.", Emoji.STUCK_OUT_TONGUE).build());
                }).orElseThrow(IllegalStateException::new); // should not have missing crew
    }

}
