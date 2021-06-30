package com.quest.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class Crew implements Comparable<Crew> {
    private final int uid;
    private String name;

    public Crew(int uid, String name) {
        requireNonNull(name);
        this.uid = uid;
        this.name = name;
    }

    public static Crew of(final int uid, final String name) {
        return new Crew(uid, name);
    }

    public void rename(final String name) {
        this.name = name;
    }

    @Override
    public int compareTo(final Crew o) {
        if (this == o || this.uid == o.uid) {
            return 0;
        } else if (uid < o.uid) {
            return -1;
        } else return 1;
    }

    public int uid() {
        return uid;
    }

    public String name() {
        return name;
    }

    public String definiteName() {
        final String capitalizedName = StringUtils.capitalize(name);
        return String.format(name.toLowerCase(Locale.ROOT).startsWith("the") ? capitalizedName : "The " + capitalizedName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Crew) obj;
        return this.uid == that.uid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public String toString() {
        return "Crew[" +
                "uid=" + uid + ", " +
                "name=" + name + ']';
    }

}
