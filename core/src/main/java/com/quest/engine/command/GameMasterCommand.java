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
public class GameMasterCommand extends Command.ArgumentCommand {
    private static final String NAME = "gamemaster";
    private static final String HELP_TEXT = "Makes you a GameMaster if you know the password";
    private static final String[] KEYWORDS = { "gamemaster", "gm" };

    public GameMasterCommand() {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, final Race race, final IOService ioService, final Explorer explorer,
                                                  final String[] arguments) {
        return respondWith(
                Optional.ofNullable(arguments[0])
                .map(password -> {
                    if (password.equals("1973")) {
                        explorer.setGameMaster(true);
                        return Message.newBuilder().append("Ok, welcome GameMaster!\n").build();
                    } else {
                        return Message.newBuilder().append("Sorry, no soup for you! %s\n", Emoji.LOCK).build();
                    }
                })
                .orElse(Message.newBuilder().append("Something unexpected has happened.\n").build()));
    }
}
