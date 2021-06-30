package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Message;
import com.quest.model.Crew;
import com.quest.model.Explorer;
import com.quest.model.Race;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CrewCommand extends Command.NoArgumentCommand {
    static final String NAME = "crew";
    static final String HELP_TEXT = "Shows you a list of crews you can join for the race; for example, type 'crew' (Hint: You use the JOIN command to join a crew.)";
    static final String[] KEYWORDS = { "crew", "team", "crews", "teams" };

    public CrewCommand() {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> evaluatePreconditions(Engine engine, Race race, IOService ioService, Explorer explorer, Optional<String> arguments) {
        return super.evaluatePreconditions(engine, race, ioService, explorer, arguments)
                .or(() -> (!explorer.isGameMaster() && (race.getCrew(explorer).isPresent() && race.getState() != Race.State.NOT_STARTED)) ?
                respondWith(Message.newBuilder().append("You can't change crews once the race has begun!").build()) :
                Optional.empty());
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, final Race race, final IOService ioService, final Explorer explorer) {
        final Message.Builder builder = Message.newBuilder().append("Here are the crews you can choose to join:\n");
        race.crews().stream()
                .sorted()
                .forEach(crew -> builder.append("%d - %s\n", crew.uid(), crew.definiteName()));
        builder.append("\nTo join a crew, use the join command; for example, type 'join 1' to join the first crew. ");
        builder.append("(By the way, you can change the name of your crew after you join.)");
        return respondWith(builder.build());
    }
}
