package org.example.proiectjava.dto;

import java.util.List;

public class EditProfessorRequest {
    private String jwt;
    private int professorID;
    private String firstName;
    private String lastName;
    private String rank;
    private List<String> courses;

    // Getters and setters
    public String getJWT() {
        return jwt;
    }

    public void setJWT(String jwt) {
        this.jwt = jwt;
    }

    public int getProfessorID() {
        return professorID;
    }

    public void setProfessorID(int professorID) {
        this.professorID = professorID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }

    public String getRank() {
        return this.rank;
    }
}
