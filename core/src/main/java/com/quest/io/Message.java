package com.quest.io;

import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.Random;

import static java.util.Objects.requireNonNull;

public class Message {

    private final static Random RANDOM = new Random();

    final private String text;
    final private String photoUrl;

    private Message(final String text, final String photoUrl) {
        this.text = requireNonNull(text);
        this.photoUrl = requireNonNull(photoUrl);
    }

    private Message(final String text) {
        this.text = requireNonNull(text);
        this.photoUrl = null;
    }

    public String text() {
        return text;
    }

    public String photoUrl() {
        return photoUrl;
    }

    public static Message of(final String text, final String photoUrl) {
        return new Message(text, photoUrl);
    }

    public static Message of(final String text) {
        return new Message(text);
    }

    public static Message of(final String template, final Object... tokens) {
        return of(String.format(template, tokens));
    }

    public static StringBuilder text(final String text, Object... tokens) {
        return new StringBuilder(String.format(text, tokens));
    }

    public static StringBuilder text(final StringBuilder stringBuilder, final String text, Object... tokens) {
        return stringBuilder.append(String.format(text, tokens));
    }

    public static String pickRandom(final String... messages) {
        requireNonNull(messages);
        if (messages.length > 0) {
            return messages[RANDOM.nextInt(messages.length)];
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", photoUrl=" + photoUrl +
                '}';
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        final StringBuilder stringBuilder;
        String photoUrl;

        Builder() {
            this.stringBuilder = new StringBuilder();
        }

        public Builder append(final String text) {
            this.stringBuilder.append(text);
            return this;
        }

        public Builder append(final StringBuilder stringBuilder) {
            return append(stringBuilder.toString());
        }

        public Builder append(final String text, final Object... tokens) {
            return append(String.format(text, tokens));
        }

        public Builder withUrl(final String photoUrl) {
            this.photoUrl = photoUrl;
            return this;
        }

        public Message build() {
            if (photoUrl != null) {
                return Message.of(stringBuilder.toString(), photoUrl);
            } else {
                return Message.of(stringBuilder.toString());
            }
        }
    }

}
