package com.example.school_project;

public class Course {
    private String name;
    private int hours;
    private String type;
    private String teacher;

    public Course(String name, int hours, String type, String teacher) {
        this.name = name;
        this.hours = hours;
        this.type = type;
        this.teacher = teacher;
    }

    public String getName() {
        return name;
    }

    public int getHours() {
        return hours;
    }

    public String getType() {
        return type;
    }

    public String getTeacher() {
        return teacher;
    }
}
