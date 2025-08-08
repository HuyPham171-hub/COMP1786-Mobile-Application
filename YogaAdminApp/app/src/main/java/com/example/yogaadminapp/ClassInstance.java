package com.example.yogaadminapp;

public class ClassInstance {
    private int id;                 // Unique ID (primary key in SQLite)
    private int courseId;          // Foreign key to Course table
    private String courseType;     // For display (Flow Yoga, etc.)
    private String date;           // e.g., 17/10/2025
    private String teacher;        // e.g., John Smith
    private String comment;        // Optional

    public ClassInstance(int id, int courseId, String courseType, String date, String teacher, String comment) {
        this.id = id;
        this.courseId = courseId;
        this.courseType = courseType;
        this.date = date;
        this.teacher = teacher;
        this.comment = comment;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseType() {
        return courseType;
    }

    public String getDate() {
        return date;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getComment() {
        return comment;
    }

    // Setter methods
    public void setDate(String date) {
        this.date = date;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
