package com.quest.engine.command;

import com.quest.engine.Engine;
import com.quest.engine.IOService;
import com.quest.io.Emoji;
import com.quest.io.Message;
import com.quest.model.*;

import java.util.Optional;

public class AnswerCommand extends Command.CommandSupport {
    static final String NAME = "answer";
    static final String HELP_TEXT = "An internal command for handling waypoint clue answers from explorers";
    static final String[] KEYWORDS = { "_answer" };

    public AnswerCommand() {
        super(NAME, HELP_TEXT, NON_PRIVILEGED, KEYWORDS);
    }

    @Override
    public Optional<Message> onCommand(final Engine engine, final Race race, final IOService ioService, final Explorer explorer,
                                       final String input) {
        return race.getCrew(explorer)
                .flatMap(race::getWaypoint)
                .map(waypoint -> waypoint.lock().test(input) ?
                        onWaypointUnlocked(race, ioService, explorer, input, waypoint) :
                        onWaypointKeyFailed(race, ioService, explorer, input, waypoint))
                .orElse(Optional.empty());
    }

    private Optional<Message> onWaypointKeyFailed(final Race race, final IOService ioService, final Explorer explorer,
                                                  final String input, final Waypoint waypoint) {
        final Message.Builder builder = Message.newBuilder();
        final String randomMessage = Message.pickRandom(WAYPOINT_INCORRECT_KEY_PROVIDED);
        final Crew crew = race.getCrew(explorer).get();
        builder.append("%s tried to unlock the waypoint with '%s'.\n", explorer.name(), input)
                .append(randomMessage);
        broadcastToCrewExcept(race, ioService, crew, builder.build(), explorer);
        broadcastGameMasters(race, ioService, Message.newBuilder().append("%s of crew %s tried unsuccessfully to unlock a waypoint " +
                "with '%s'", explorer.name(), crew.name(), input).build());
        return respondWith(Message.newBuilder().append(randomMessage).build());
    }

    private Optional<Message> onWaypointUnlocked(final Race race, final IOService ioService, final Explorer explorer,
                                                 final String input, final Waypoint waypoint) {
        final Message.Builder builder = Message.newBuilder();
        final String randomMessage = Message.pickRandom(WAYPOINT_CORRECT_KEY_PROVIDED);
        final Crew crew = race.getCrew(explorer).get();
        final Quest.Course course = race.getCourse(crew).get();

        builder.append("%s tried to unlock the waypoint with '%s'.\n", explorer.name(), input)
                .append(randomMessage);
        if (course.isLastWaypoint(waypoint)) {
            race.unsetWaypoint(crew);
            builder.append("\n\nThat was the last one! Hurry back to the starting point to secure victory! %s",
                    Emoji.TROPHY);
            builder.withUrl("https://stizza-bongo-8997.twil.io/assets/CongratulationsSmaller.jpg");
            broadcastToCrew(race, ioService, crew, builder.build());
        } else {
            final Waypoint nextWaypoint = course.nextWaypoint(waypoint);
            race.setWaypoint(crew, nextWaypoint);
            broadcastToCrew(race, ioService, crew, builder.append("\n\nHere comes the next one...\n").build());
            broadcastWaypointClue(race, ioService, crew);
        }

        broadcastGameMasters(race, ioService, Message.newBuilder().append("%s of crew %s successfully unlocked a waypoint " +
                "with '%s'", explorer.name(), crew.name(), input).build());

        return respondWith(Message.newBuilder().append("\n(And thanks %s for sending the right key for that last waypoint!) " +
                Emoji.HUNDRED_PERCENT, explorer.name()).build());
    }

    @Override
    protected Optional<Message> internalOnCommand(Engine engine, Race race, IOService ioService, Explorer explorer,
                                                  Optional<String> arguments) {
        return Optional.empty();
    }

    private final static String[] WAYPOINT_CORRECT_KEY_PROVIDED = {
            "Yes! You got it! " + Emoji.CLAPPING,
            "Great! That's the key to the waypoint lock! " + Emoji.HIGH_FIVE,
            "Fantastic! You've unlocked the waypoint! " + Emoji.FIRE,
            "Good work! You've busted the lock of the waypoint! " + Emoji.UNLOCK,
            "Well done, crew! You've conquered that waypoint. " + Emoji.PARTY
    };

    private final static String[] WAYPOINT_INCORRECT_KEY_PROVIDED = {
            "Sorry, that's not the key to the waypoint. Try again! " + Emoji.THUMBS_DOWN,
            "Too bad, that's not the right key. Keep trying! " + Emoji.CROSSMARK,
            "Nope, sorry -- that key doesn't unlock this waypoint. Try again! " + Emoji.DISAPPOINTED_FACE,
            "Darn, that's not the right key! Try again. " + Emoji.POUTING_FACE
    };

}
