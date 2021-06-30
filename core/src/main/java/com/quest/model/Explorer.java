package com.quest.model;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public final class Explorer {
    private final String uid;
    private String name;
    private boolean isGameMaster;


    public Explorer(final String uid, final String name) {
        requireNonNull(uid);
        requireNonNull(name);
        this.uid = uid;
        this.name = name;
    }

    public static Explorer of(final String uid, final String name) {
        return new Explorer(uid, name);
    }

    public static Explorer of(final String uid) {
        return of(uid, uid);
    }

    public String uid() {
        return uid;
    }

    public String name() {
        return name;
    }

    public void rename(final String name) { this.name = name; }

    public void setGameMaster(boolean isGameMaster) {
        this.isGameMaster = isGameMaster;
    }

    public boolean isGameMaster() {
        return isGameMaster;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Explorer explorer = (Explorer) o;
        return Objects.equals(uid, explorer.uid); // UID is king
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid);
    }

    @Override
    public String toString() {
        return "Explorer{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", isGameMaster=" + isGameMaster +
                '}';
    }

}