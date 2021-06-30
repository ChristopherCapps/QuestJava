package com.quest.model;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class Race {

    public enum State {
        NOT_STARTED,
        IN_PROGRESS,
        COMPLETED
    }

    private State state;
    private final Set<Explorer> explorers;
    private final Set<Crew> crews;
    private final Map<Explorer, Crew> explorerToCrewMap;
    private final Map<Crew, Quest.Course> crewToCourseMap;
    private final Map<Crew, Waypoint> crewToWaypointMap;
    private final Quest quest;
    private Instant startTime;

    protected Race(final Quest quest) {
        state = State.NOT_STARTED;
        explorers = new HashSet<>();
        crews = new HashSet<>();
        explorerToCrewMap = new HashMap<>();
        crewToCourseMap = new HashMap<>();
        crewToWaypointMap = new HashMap<>();
        this.quest = quest;
    }

    public static Race of(final Quest quest) { return new Race(quest); }

    public void updateStartTime() {
        this.startTime = Instant.now();
    }

    public Instant getStartTime() {
        return startTime;
    }

    public long durationMinutes() {
        return Duration.between(startTime, Instant.now()).toMinutes();
    }
    
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = requireNonNull(state);
    }
    
    public Set<Explorer> explorers() {
        return explorers;
    }

    public Set<Crew> crews() {
        return crews;
    }

    public Optional<Crew> crewById(final int uid) {
        return crews.stream().filter(crew -> crew.uid() == uid).findFirst();
    }

    public Optional<Explorer> getExplorer(final String uid) {
        return explorers.stream()
                .filter(explorer -> explorer.uid().equals(uid))
                .findFirst();
    }

    public Optional<Crew> getCrew(final String name) {
        return crews.stream()
                .filter(crew -> crew.name().equals(name))
                .findFirst();
    }

    public Crew addExplorer(final Explorer explorer, final Crew crew) {
        explorerToCrewMap.put(explorer, crew);
        return crew;
    }

    public void addExplorer(final Explorer explorer) {
        explorers.add(explorer);
    }

    public void addCrew(final Crew crew) {
        crews.add(crew);
    }

    public Optional<Crew> getCrew(final Explorer explorer) {
        return Optional.ofNullable(explorerToCrewMap.get(explorer));
    }

    public Optional<Crew> removeExplorer(final Explorer explorer) {
        explorers.remove(explorer);
        return getCrew(explorer)
                .map(crew -> {
                    explorerToCrewMap.remove(explorer);
                    return crew;
                });
    }

    public Set<Explorer> getExplorers(final Crew crew) {
        return explorerToCrewMap.keySet()
                .stream().filter(explorer -> getCrew(explorer).map(c -> c == crew).orElse(Boolean.FALSE))
                .collect(Collectors.toSet());
    }

    public void setCourse(final Crew crew, final Quest.Course course) {
        crewToCourseMap.put(crew, course);
    }

    public Optional<Quest.Course> getCourse(final Crew crew) {
        return Optional.ofNullable(crewToCourseMap.get(crew));
    }

    public void unsetCourse(Crew crew) {
        if (crewToCourseMap.containsKey(crew)) {
            crewToCourseMap.remove(crew);
        }
    }

    public Optional<Waypoint> getWaypoint(final Crew crew) {
        return Optional.ofNullable(crewToWaypointMap.get(crew));
    }
    
    public void setWaypoint(final Crew crew, final Waypoint waypoint) {
        crewToWaypointMap.put(crew, waypoint);
    }

    public void unsetWaypoint(final Crew crew) {
        crewToWaypointMap.remove(crew);
    }
    
    public Quest quest() {
        return quest;
    }

}
