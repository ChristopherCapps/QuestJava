package com.quest.engine.command.privileged;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.engine.command.Command;
import com.quest.io.Emoji;
import com.quest.io.Message;
import com.quest.model.Explorer;
import com.quest.model.Race;

import java.util.Optional;

public class RestartCommand extends Command.NoArgumentCommand {

    static final String NAME = "restart";
    static final String HELP_TEXT = "Restarts a race";
    static final String[] KEYWORDS = { "restart" };

    public RestartCommand() {
        super(NAME, HELP_TEXT, PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService, Explorer explorer) {
        broadcastToExplorers(race, ioService, Message.newBuilder().append("All explorers take note! The" +
                " GameMaster is restarting this race. You will need to rejoin, I'm afraid. Sorry! %s", Emoji.CHICKEN).build());
        engine.reset();
        return respondWith(Message.of("Race restarted.\n"));
    }
}
