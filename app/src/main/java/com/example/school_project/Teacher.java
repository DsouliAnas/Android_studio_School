package com.example.school_project;

public class Teacher {
    private int id;
    private String name;
    private String email;

    public Teacher(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Teacher(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
