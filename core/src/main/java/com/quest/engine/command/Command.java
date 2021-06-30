package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Message;
import com.quest.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public interface Command {

    boolean matchesInput(String input);

    boolean isPrivileged();

    String helpText();

    String name();

    Optional<Message> onCommand(Engine engine, Race race, IOService ioService, Explorer explorer, String input);

    abstract class CommandSupport implements Command {

        static final Logger logger = LoggerFactory.getLogger(Command.class);

        protected static final boolean PRIVILEGED = true;
        protected static final boolean NON_PRIVILEGED = false;

        private final String name;
        private final String helpText;
        private final boolean isPrivileged;
        private final Set<String> keywords;

        protected CommandSupport(final String name, final String helpText, final boolean isPrivileged,
                                 final String... keywords) {
            this.name = name;
            this.helpText = helpText;
            this.isPrivileged = isPrivileged;
            this.keywords = new HashSet<>(Arrays.asList(keywords));
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public String helpText() {
            return helpText;
        }

        protected Set<String> keywords() {
            return keywords;
        }

        protected Optional<Message> evaluateExplorerHasACrewPrecondition(final Engine engine, final Race race,
                                                                         final IOService ioService, final Explorer explorer,
                                                                         final Optional<String> arguments) {
            return (race.getCrew(explorer).isEmpty() ?
                    respondWith(Message.newBuilder().append("You first need to be a member of a crew! " +
                        "Type 'crew' to see a list of crews you can join.").build()) :
                    Optional.empty());
        }

//        protected Optional<Message> evaluateRaceInProgressPrecondition(final Engine engine, final Race race,
//                                                                  final IOService ioService, final Explorer explorer,
//                                                                  final Optional<String> arguments) {
//            return (race.getState() == Race.State.IN_PROGRESS ?
//                    Optional.empty() :
//                    respondWith(Message.newBuilder().append("But the race is not currently underway!").build()));
//        }
//
//        protected Optional<Message> evaluateRaceNotStartedPrecondition(final Engine engine, final Race race,
//                                                                       final IOService ioService, final Explorer explorer,
//                                                                       final Optional<String> arguments) {
//            return (race.getState() == Race.State.NOT_STARTED ?
//                    Optional.empty() :
//                    respondWith(Message.newBuilder().append("But the race is currently underway!").build()));
//        }

        protected Optional<Message> evaluatePreconditions(final Engine engine, final Race race, final IOService ioService,
                                                          final Explorer explorer, final Optional<String> arguments) {
            return (isPrivileged && !explorer.isGameMaster()) ?
                    respondWith(Message.newBuilder().append("Sorry, but you don't have authority to execute " +
                            "this command.").build()) :
                    Optional.empty();
        }

        protected Optional<Pair<String, Optional<String>>> matchInput(final String input) {
            logger.debug("Trying to match {} for input '{}'", getClass().getSimpleName(), input);
            final String normalizedInput = input.trim();
            final Optional<String> command = keywords.stream()
                    .map(String::toLowerCase)
                    .sorted((f1, f2) -> String.CASE_INSENSITIVE_ORDER.compare(f2, f1))
                    .filter(keyword -> normalizedInput.toLowerCase(Locale.ROOT).startsWith(keyword))
                    .findFirst();
            if (command.isPresent()) {
                logger.info("Matched keyword {} of {} for input '{}'", command.get(), this.getClass().getSimpleName(), input);
                final Optional<String> arguments = normalizedInput.length() > command.get().length() ?
                        Optional.of(normalizedInput.substring(command.get().length())) :
                        Optional.empty();
                return Optional.of(Pair.of(command.get(), arguments));
            } else {
                logger.debug("No match for {} for input '{}'", getClass().getSimpleName(), input);
                return Optional.empty();
            }
        }

        @Override
        public boolean matchesInput(final String input) {
            return matchInput(input).isPresent();
        }

        @Override
        public boolean isPrivileged() {
            return isPrivileged;
        }

        protected Optional<String> getArguments(final String input) {
            return matchInput(input)
                    .map(Pair::getRight)
                    .orElse(Optional.empty());
        }

        protected void broadcastToCrewExcept(final Race race, final IOService ioService, final Crew crew,
                                             final Message message, final Explorer explorer) {
            race.getExplorers(crew).stream().forEach(e -> {
                if (explorer == null || !e.equals(explorer)) {
                    ioService.sendMessage(e.uid(), message);
                }
            });
        }

        protected void broadcastToCrew(final Race race, final IOService ioService, final Crew crew, final Message message) {
            broadcastToCrewExcept(race, ioService, crew, message, null);
        }

        protected void broadcastToExplorers(final Race race, final IOService ioService, final Message message) {
            ioService.broadcastMessage(message, allExplorerIds(race));
        }

        protected void broadcastGameMasters(final Race race, final IOService ioService, final Message message) {
            race.explorers().stream().filter(Explorer::isGameMaster).forEach(gm ->
                ioService.sendMessage(gm.uid(), message));
        }

        protected Message buildWaypointMessage(final Quest.Course course, final Waypoint waypoint) {
            return Message.newBuilder().append("WAYPOINT %d\n", course.waypointIndex(waypoint))
                    .append(waypoint.clue().text())
                    .withUrl(waypoint.clue().photoUrl()).build();
        }

        protected void broadcastWaypointClue(final Race race, final IOService ioService, final Crew crew) {
            race.getCourse(crew)
                .ifPresent(course -> race.getWaypoint(crew).ifPresentOrElse(
                        waypoint -> broadcastToCrew(race, ioService, crew, buildWaypointMessage(course, waypoint)),
                        () -> {}));
        }

        protected void broadcastWaypointClues(final Race race, final IOService ioService ) {
            race.crews().forEach(crew -> broadcastWaypointClue(race, ioService, crew));
        }

        private String[] allExplorerIds(final Race race) {
            return race.explorers().stream().map(Explorer::uid).toArray(String[]::new);
        }


        @Override
        public Optional<Message> onCommand(Engine engine, final Race race, final IOService ioService, final Explorer explorer,
                                           final String input) {
            final Optional<String> arguments = matchInput(input).get().getRight();
            logger.debug("Executing {} with arguments '{}'", getClass().getSimpleName(), arguments);
            return evaluatePreconditions(engine, race, ioService, explorer, arguments)
                    .or(() -> internalOnCommand(engine, race, ioService, explorer, arguments));
        }

        protected Optional<Message> respondWith(final Message message) {
            return Optional.of(message);
        }


        protected abstract Optional<Message> internalOnCommand(final Engine engine, final Race race, IOService ioService,
                                                               Explorer explorer, Optional<String> arguments);
    }

    abstract class NoArgumentCommand extends CommandSupport {
        protected NoArgumentCommand(String name, String helpText, boolean isPrivileged, String... keywords) {
            super(name, helpText, isPrivileged, keywords);
        }

        @Override
        protected Optional<Message> internalOnCommand(Engine engine, final Race race, final IOService ioService,
                                                      final Explorer explorer, final Optional<String> arguments) {
            return (arguments.isPresent() && arguments.get().trim().length() > 0) ?
                    respondWith(Message.newBuilder().append("It looks like you're trying to use the %s command, but " +
                            "I didn't expect to see any other text following it. Here's some help for that command:\n\n%s\n%s",
                            name().toUpperCase(Locale.ROOT), name().toUpperCase(Locale.ROOT), helpText()).build()) :
                    internalOnCommand(engine, race, ioService, explorer);
        }

        protected abstract Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService,
                                                               Explorer explorer);
    }

    abstract class ArgumentCommand extends CommandSupport {
        protected ArgumentCommand(String name, String helpText, boolean isPrivileged, String... keywords) {
            super(name, helpText, isPrivileged, keywords);
        }

        @Override
        protected Optional<Message> internalOnCommand(Engine engine, final Race race, final IOService ioService,
                                                      final Explorer explorer, final Optional<String> arguments) {
            return (arguments.isPresent() && arguments.get().trim().length() > 0) ?
                    internalOnCommand(engine, race, ioService, explorer, StringUtils.split(arguments.get().trim(), " ")) :
                    respondWith(Message.newBuilder().append("It looks like you're trying to use the %s command, but " +
                            "I don't quite understand. Here's some help for that command:\n\n%s - %s",
                            name().toUpperCase(Locale.ROOT), name().toUpperCase(Locale.ROOT), helpText()).build());
        }

        protected abstract Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService, Explorer explorer,
                                                               String[] arguments);
    }
}
