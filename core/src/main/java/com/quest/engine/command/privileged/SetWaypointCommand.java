package com.quest.engine.command.privileged;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.engine.command.Command;
import com.quest.io.Message;
import com.quest.model.*;

import java.util.Optional;

public class SetWaypointCommand extends Command.ArgumentCommand {
    static final String NAME = "setwaypoint";
    static final String HELP_TEXT = "Sets the waypoint for a crew";
    static final String[] KEYWORDS = {"setw", "setwaypoint"};

    public SetWaypointCommand() {
        super(NAME, HELP_TEXT, PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService, Explorer explorer, String[] arguments) {
        final Message.Builder message = Message.newBuilder();
        if (arguments.length != 2) {
            return respondWith(message.append("Invalid arguments provided: you must specify the crew ID and waypoint ID to set.").build());
        }

        final int crewId;
        final int waypointId;

        try {
            crewId = Integer.parseInt(arguments[0].trim());
            waypointId = Integer.parseInt(arguments[1].trim());
        }
        catch (NumberFormatException nfE) {
            return respondWith(message.append("The crew ID and waypoint ID must be numbers.").build());
        }

        final Optional<Crew> crew = race.crewById(crewId);
        if (crew.isPresent()) {
            final Optional<Quest.Course> course = race.getCourse(crew.get());
            if (course.isPresent()) {
                final Optional<Waypoint> waypoint = course.get().waypointAtIndex(waypointId);
                if (waypoint.isPresent()) {
                    race.setWaypoint(crew.get(), waypoint.get());
                    message.append("Crew %d (%s) is now at waypoint %d (%s)", crew.get().uid(), crew.get().name(),
                            waypointId, waypoint.get().clue().text());
                } else {
                    message.append("The waypoint with ID %d does not exist.", waypointId);
                }
            } else {
                message.append("Unable to locate course for crew %d.", crewId);
            }
        } else {
            message.append("Unable to locate a crew with ID %d.", crewId);
        }

        return respondWith(message.build());
    }

}
