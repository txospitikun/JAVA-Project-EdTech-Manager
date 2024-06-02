package org.example.proiectjava.service;

public class Course {
    private final int id;
    private final String title;
    private final int year;

    public Course(int id, String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }
}
