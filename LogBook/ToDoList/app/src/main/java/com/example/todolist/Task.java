package com.example.todolist;

import java.util.Date;

public class Task {
    public String name;
    public Date deadline;
    public int duration;
    public String descriptions;
    public boolean completed;
    public Task(String n, Date dl, int d, String des, boolean completed){
        name = n;
        deadline = dl;
        duration = d;
        descriptions = des;
        this.completed = completed;
    }
}
