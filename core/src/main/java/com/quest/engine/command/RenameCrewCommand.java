package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Emoji;
import com.quest.io.Message;
import com.quest.model.Explorer;
import com.quest.model.Race;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RenameCrewCommand extends Command.ArgumentCommand {
    private static final String NAME = "rename crew";
    private static final String HELP_TEXT = "Renames your crew; for example, type 'rename crew The Monkeys'";
    private static final String[] KEYWORDS = { "rename crew" };

    public RenameCrewCommand() {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> evaluatePreconditions(Engine engine, Race race, IOService ioService, Explorer explorer, Optional<String> arguments) {
        return super.evaluatePreconditions(engine, race, ioService, explorer, arguments)
                .or(() -> evaluateExplorerHasACrewPrecondition(engine, race, ioService, explorer, arguments));
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, final Race race, final IOService ioService, final Explorer explorer,
                                                  final String[] arguments) {
        return respondWith(
                Optional.of(arguments[0])
                    .map(name ->
                        race.getCrew(explorer)
                                .map(crew -> {
                                    final String currentName = crew.definiteName();
                                    crew.rename(name);
                                    return Message.newBuilder().append("Ok, I've renamed your crew from %s to %s. " +
                                                    "Sounds great! %s", currentName, crew.definiteName(), Emoji.FIRE).build();
                                })
                                .orElseThrow(IllegalStateException::new)) // Already validated crew assignment
                        .orElseThrow(IllegalStateException::new)); // Shouldn't be possible for the arg to be null
    }
}
