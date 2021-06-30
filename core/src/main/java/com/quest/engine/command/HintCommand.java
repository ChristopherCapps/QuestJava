package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Message;
import com.quest.model.Crew;
import com.quest.model.Explorer;
import com.quest.model.Race;

import java.util.Optional;

public class HintCommand extends Command.NoArgumentCommand {
    static final String NAME = "hint";
    static final String HELP_TEXT = "Asks the Game Master to send you a hint for your current waypoint";
    static final String[] KEYWORDS = { "hint" };

    public HintCommand() {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
    }

        @Override
    protected Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService, Explorer explorer) {
        return respondWith(
                race.getCrew(explorer)
                        .map(crew -> {
                            broadcastGameMasters(race, ioService, Message.newBuilder()
                                    .append("Explorer %s from crew %s has requested a hint.", explorer.name(),
                                            crew.definiteName()).build());
                            return Message.newBuilder().append("Your request for a hint has been sent.").build();
                        })
                        .orElse(Message.newBuilder().append("But it doesn't appear you're on a team!").build()));
    }
}
