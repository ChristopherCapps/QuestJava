package com.quest.engine.command;

import org.springframework.context.annotation.ComponentScan;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@ComponentScan
public class CommandRegistry {
    private List<Command> commands;

    public CommandRegistry(final List<Command> commands) {
        this.commands = requireNonNull(commands);
    }

    public Collection<Command> commands() {
        return commands;
    }

    public void addCommand(final Command command) {
        if (!commands.contains(command)) {
            commands.add(command);
        }
    }

    public Optional<Command> tryMatchCommand(final String input) {
        return commands().stream()
                .filter(command -> command.matchesInput(input))
                .findFirst();
    }
}
