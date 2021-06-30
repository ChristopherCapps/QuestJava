package com.quest.model;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class Quest {

    final private String name;
    final private List<Course> courses; // leave as Array?

    public Quest(final String name, final Course... courses) {
        this.name = requireNonNull(name);
        this.courses = Arrays.asList(requireNonNull(courses));
    }

    
    public String name() {
        return name;
    }
    
    public Collection<Course> courses() {
        return this.courses;
    }

    public static Quest of(final String name, final Quest.Course... courses) {
        return new Quest(name, courses);
    }
    
    public String toString() {
        return "Quest{" +
                "name='" + name + '\'' +
                ", courses=" + courses +
                '}';
    }

    // ***** Course ******
    public static class Course {
        final private String name;
        final private LinkedList<Waypoint> waypoints;

        public Course(final String name, final Waypoint... waypoints) {
            this(name, Arrays.asList(requireNonNull(waypoints)));
        }

        public Course(final String name, final List<Waypoint> waypoints) {
            this.name = name;
            this.waypoints = new LinkedList<>(requireNonNull(waypoints));
        }

        public static Quest.Course of(final String name, final Waypoint... waypoints) {
            return new Quest.Course(name, waypoints);
        }

        public static Quest.Course of(final String name, final List<Waypoint> waypoints) {
            return new Quest.Course(name, waypoints);
        }

        public static Quest.Course reverse(final String name, final Quest.Course course) {
            final List<Waypoint> waypoints = requireNonNull(course).waypoints();
            Collections.reverse(waypoints);
            return Quest.Course.of(name, waypoints);
        }

        public int waypointIndex(final Waypoint waypoint) {
            final var waypointsA = waypoints.toArray();
            for (int i = 0; i < waypointsA.length; i++) {
                if (waypointsA[i] == waypoint) return i+1;
            }
            return -1;
        }

        public Optional<Waypoint> waypointAtIndex(final int index) {
            final var waypointsA = waypoints.toArray();
            return (waypointsA.length == 0 || index < 1 || index > waypointsA.length) ?
                    Optional.empty() : Optional.of((Waypoint) waypointsA[index-1]);
        }

        public String name() {
            return name;
        }
        
        public Waypoint firstWaypoint() {
            return waypoints.getFirst();
        }
        
        public Waypoint lastWaypoint() {
            return waypoints.getLast();
        }
        
        public Waypoint previousWaypoint(final Waypoint waypoint) {
            final int index = waypoints.indexOf(requireNonNull(waypoint));
            if (index < 0) {
                throw new IllegalArgumentException("The Waypoint argument [" + waypoint +
                        "] is not a member of this Course [" + this + "].");
            } else if (index == 0) {
                throw new IllegalArgumentException("The given Waypoint [" + waypoint +
                        "] is the first Waypoint in this Course [" + this + "].");
            } else {
                return waypoints.get(index - 1);
            }
        }

        public Waypoint nextWaypoint(final Waypoint waypoint) {
            final int index = waypoints.indexOf(requireNonNull(waypoint));
            if (index < 0) {
                throw new IllegalArgumentException("The Waypoint argument [" + waypoint +
                        "] is not a member of this Course [" + this + "].");
            } else if (index >= waypoints.size()) {
                throw new IllegalArgumentException("The given Waypoint [" + waypoint +
                        "] is the last Waypoint in this Course [" + this + "].");
            } else {
                return waypoints.get(index + 1);
            }
        }

        public boolean isLastWaypoint(final Waypoint waypoint) {
            return requireNonNull(waypoint).equals(lastWaypoint());
        }
        
        public boolean isFirstWaypoint(final Waypoint waypoint) {
            return requireNonNull(waypoint).equals(firstWaypoint());
        }

        public boolean hasNextWaypoint(final Waypoint waypoint) {
            return waypoints.contains(requireNonNull(waypoint)) && !isLastWaypoint(waypoint);
        }
        
        public List<Waypoint> waypoints() {
            return new LinkedList<>(waypoints);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Course course = (Course) o;
            return Objects.equals(name, course.name) && Objects.equals(waypoints, course.waypoints);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, waypoints);
        }

        @Override
        public String toString() {
            return "Course{" +
                    "name='" + name + '\'' +
                    ", waypoints=" + waypoints +
                    '}';
        }
    }

}