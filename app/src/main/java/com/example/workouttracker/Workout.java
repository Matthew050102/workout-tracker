package com.example.workouttracker;

public class Workout {
    private String id;
    private String name;
    private String reps;
    private Double latitude;
    private Double longitude;


    public Workout() { }

    public Workout(String id, String name, String reps, Double latitude, Double longitude) {
        this.id = id;
        this.name = name;
        this.reps = reps;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getReps() { return reps; }
    public void setReps(String reps) { this.reps = reps; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
}
