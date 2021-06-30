package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Message;
import com.quest.model.Explorer;
import com.quest.model.Race;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Optional;

@Component
public class HelpCommand extends Command.NoArgumentCommand {
    public static HelpCommand of(final CommandRegistry registry) {
        final HelpCommand helpCommand = new HelpCommand(registry);
        registry.addCommand(helpCommand);
        return helpCommand;
    }

    static final String NAME = "commands";
    static final String HELP_TEXT = "Shows the list of commands and how they work";
    static final String[] KEYWORDS = { "help", "commands", "sos", "?" };

    private final CommandRegistry registry;

    public HelpCommand(final CommandRegistry registry) {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
        this.registry = registry;
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService, Explorer explorer) {
        final Message.Builder builder = Message.newBuilder().append("Here are some commands you can use:\n\n");
        registry.commands().stream().forEach(command -> {
            if ((explorer.isGameMaster()) || !command.isPrivileged()) {
                builder.append("%s - %s\n\n", command.name().toUpperCase(Locale.ROOT), command.helpText());
            }
        });
        return respondWith(builder.build());
    }
}
