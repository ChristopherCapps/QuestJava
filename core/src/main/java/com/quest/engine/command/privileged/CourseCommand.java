package com.quest.engine.command.privileged;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.engine.command.Command;
import com.quest.io.Message;
import com.quest.model.*;

import java.util.Arrays;
import java.util.Optional;

public class CourseCommand extends Command.ArgumentCommand {
    static final String NAME = "course";
    static final String HELP_TEXT = "Lists a course for one or more crews, given their IDs; for example, COURSE 1 2 will list the courses for crews 1 & 2.";
    static final String[] KEYWORDS = { "course" };

    public CourseCommand() {
        super(NAME, HELP_TEXT, PRIVILEGED, KEYWORDS);
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService, Explorer explorer, String[] arguments) {
        final Message.Builder message = Message.newBuilder();
        if (arguments.length < 1) {
            return respondWith(message.append("Invalid arguments provided: you must specify a list of crew IDs, separated by spaces.").build());
        }

        Arrays.stream(arguments).sequential().forEach(arg -> {
            try {
                final int crewId = Integer.parseInt(arg.trim());
                final Optional<Crew> crew = race.crewById(crewId);
                if (crew.isPresent()) {
                    final Optional<Quest.Course> course = race.getCourse(crew.get());
                    if (course.isPresent()) {
                        message.append("Waypoints for crew %d - %s\n", crew.get().uid(), crew.get().name());
                        course.get().waypoints().stream().sequential().forEach(waypoint ->
                                message.append("%d. %s\n", course.get().waypointIndex(waypoint), waypoint.clue().text()));
                        message.append("\n");
                    } else {
                        message.append("Unable to locate course for crew %d.", crewId);
                    }
                } else {
                    message.append("Unable to locate a crew with ID %d.", crewId);
                }
            }
            catch (NumberFormatException nfE) {
                message.append("Processing stopped with argument '%s' which is not a valid crew ID.", arg);
            }
        });

        return respondWith(message.build());

//        final Message.Builder message = Message.newBuilder();
//
//        Arrays.stream(arguments).sequential().forEach(arg ->
//            Optional.ofNullable(arg)
//            .map(Integer::parseInt)
//            .flatMap(race::crewById)
//                    .ifPresentOrElse(crew ->
//                            race.getCourse(crew)
//                                    .ifPresentOrElse(course -> {
//                                        message.append("Waypoints for crew %d - %s\n", crew.uid(), crew.name());
//                                        course.waypoints().stream().sequential().forEach(waypoint ->
//                                                message.append("%d. %s\n", course.waypointIndex(waypoint), waypoint.clue().text()));
//                                        message.append("\n");
//                                    },
//                                            () -> message.append("Could not find a course for crew %s", crew.uid())),
//                            () -> message.append("Could not find a crew with ID %s", arg)));
//
//        return respondWith(message.build());
    }
}
