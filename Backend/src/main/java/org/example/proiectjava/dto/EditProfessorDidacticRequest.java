package org.example.proiectjava.dto;

import java.util.List;

public class EditProfessorDidacticRequest {
    private String jwt;
    private int professorID;
    private List<String> courses;

    // Getters and setters
    public String getJWT() { return jwt; }
    public void setJWT(String jwt) { this.jwt = jwt; }

    public int getProfessorID() { return professorID; }
    public void setProfessorID(int professorID) { this.professorID = professorID; }

    public List<String> getCourses() { return courses; }
    public void setCourses(List<String> courses) { this.courses = courses; }
}
