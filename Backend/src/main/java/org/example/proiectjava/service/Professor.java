package org.example.proiectjava.service;

public class Professor {
    private final int id;
    private final String firstName;
    private final String lastName;

    public Professor(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
