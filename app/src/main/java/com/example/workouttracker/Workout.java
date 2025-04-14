package com.example.workouttracker;

public class Workout {
    private String name;
    private String reps;  // Most ismétlés számot tárolunk

    public Workout(String name, String reps) {
        this.name = name;
        this.reps = reps;
    }

    public String getName() {
        return name;
    }

    public String getReps() {
        return reps;
    }
}
