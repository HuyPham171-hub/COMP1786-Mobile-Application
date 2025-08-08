package com.example.yogaadminapp;

public class Course {
    private int id;
    private String dayOfWeek;
    private String time;
    private int capacity;
    private int duration;
    private double price;
    private String type;
    private String description;
    private String skillLevel;

    // Constructor
    public Course(int id, String dayOfWeek, String time, int capacity, int duration,
                  double price, String type, String description, String skillLevel) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.capacity = capacity;
        this.duration = duration;
        this.price = price;
        this.type = type;
        this.description = description;
        this.skillLevel = skillLevel;
    }

    // Optional constructor with teacher/date for search/display
    public Course(int id, String dayOfWeek, String time, int capacity, int duration,
                  double price, String type, String description, String skillLevel,
                  String teacher, String date) {
        this(id, dayOfWeek, time, capacity, duration, price, type, description, skillLevel);
    }

    // Getters
    public int getId() { return id; }
    public String getDayOfWeek() { return dayOfWeek; }
    public String getTime() { return time; }
    public int getCapacity() { return capacity; }
    public int getDuration() { return duration; }
    public double getPrice() { return price; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getSkillLevel() { return skillLevel; }
}
