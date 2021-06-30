package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Message;
import com.quest.model.Explorer;
import com.quest.model.Race;

import java.util.Optional;

public class RemoveCommand extends Command.NoArgumentCommand {
    static final String NAME = "remove";
    static final String HELP_TEXT = "Removes you from the race; for example, type 'remove'";
    static final String[] KEYWORDS = { "remove", "unjoin" };

    public RemoveCommand() {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService, Explorer explorer) {
        race.removeExplorer(explorer);
        return respondWith(Message.newBuilder()
                .append("You've been removed from the race. ")
                .append("You can always rejoin by texting me back with a 'hi'! Cya.").build());
    }
}
