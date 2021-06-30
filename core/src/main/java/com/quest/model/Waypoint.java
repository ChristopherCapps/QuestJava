package com.quest.model;

import com.quest.io.Message;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

public record Waypoint(String uid, Message clue, Waypoint.Lock lock) {

    public Waypoint {
        requireNonNull(uid);
        requireNonNull(clue);
        requireNonNull(lock);
    }

    public static Waypoint of(final String uid, final Message clue, final Waypoint.Lock lock) {
        return new Waypoint(uid, clue, lock);
    }

    public static Waypoint of(final Message clue, final Waypoint.Lock lock) {
        return Waypoint.of(UUID.randomUUID().toString(), clue, lock);
    }

    // ***** Key ******
    public record Key(String... values) implements Predicate<String> {
        public Key {
            requireNonNull(values);
        }

        protected String normalize(final String input) {
            return input.toLowerCase(Locale.ROOT).trim();
        }

        @Override
        public boolean test(final String input) {
            final String normalizedInput = normalize(input);
            // After normalizing input, we see if any key words show up anywhere in the input
            return Arrays.asList(values).stream().anyMatch(value -> normalizedInput.contains(value));
        }

        public static Waypoint.Key of(final String value) {
            return new Waypoint.Key(value);
        }

        public static Waypoint.Key of(final String... values) {
            return new Waypoint.Key(values);
        }

        @Override
        public String toString() {
            return "Key{" +
                    "values=" + Arrays.toString(values) +
                    '}';
        }
    }

    // ***** Lock ******
    public record Lock(Waypoint.Key... keys) implements Predicate<String> {
        public Lock {
            requireNonNull(keys);
        }

        @Override
        public boolean test(final String input) {
            return Arrays.stream(keys).sequential().anyMatch(key -> key.test(input));
        }

        public static Waypoint.Lock of(final Waypoint.Key... keys) {
            return new Waypoint.Lock(keys);
        }

        public static Waypoint.Lock of(final String... keyValues) {
            return Waypoint.Lock.of(Waypoint.Key.of(keyValues));
        }

        @Override
        public String toString() {
            return "Lock{" +
                    "keys=" + Arrays.toString(keys) +
                    '}';
        }
    }

}