package com.quest.engine.command.privileged;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.engine.command.Command;
import com.quest.io.Message;
import com.quest.model.Explorer;
import com.quest.model.Race;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

public class StatusCommand extends Command.NoArgumentCommand {
    static final String NAME = "status";
    static final String HELP_TEXT = "Summarizes a race";
    static final String[] KEYWORDS = { "status" };

    public StatusCommand() {
        super(NAME, HELP_TEXT, PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService, Explorer explorer) {
        final Message.Builder builder = Message.newBuilder().append("%s\nStarted at %s (%d minutes)\n",
                race.quest().name(), DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT )
                        .withLocale( Locale.US )
                        .withZone( ZoneId.systemDefault()).format(race.getStartTime()), race.durationMinutes());
        race.crews().stream().sorted().forEach(crew -> {
            builder.append("\nCrew %d: %s\n", crew.uid(), crew.definiteName());
            race.getExplorers(crew).forEach(e -> builder.append(" %s: %s\n", e.uid(), e.name()));
            builder.append("\nWaypoint");
            race.getWaypoint(crew).ifPresentOrElse(
                    waypoint -> builder.append(" (%d of %d): %s\n[Keys: %s]\n",
                            race.getCourse(crew).get().waypointIndex(waypoint),
                            race.getCourse(crew).get().waypoints().size(),
                            waypoint.clue().text(),
                            Arrays.toString(waypoint.lock().keys())),
                    () -> builder.append(": None\n"));
        });

        return respondWith(builder.build());
    }

}
