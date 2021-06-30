package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Emoji;
import com.quest.io.Message;
import com.quest.model.Explorer;
import com.quest.model.Race;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NameCommand extends Command.ArgumentCommand {
    static final String NAME = "name";
    static final String HELP_TEXT = "Changes your name, for example type 'name Chris'";
    static final String[] KEYWORDS = { "name" };

    public NameCommand() {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, final Race race, final IOService ioService, final Explorer explorer,
                                                  final String[] arguments) {
        final String newName = StringUtils.joinWith(" ", arguments);
        explorer.rename(newName);
        return respondWith(Message.newBuilder().append("Ok, nice to meet you, %s! %s",newName,
                Emoji.THUMBS_UP ).build());
    }
}
