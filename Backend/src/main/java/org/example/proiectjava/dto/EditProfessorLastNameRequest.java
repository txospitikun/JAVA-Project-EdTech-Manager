package org.example.proiectjava.dto;

public class EditProfessorLastNameRequest {
    private String jwt;
    private int professorID;
    private String lastName;

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public int getProfessorID() {
        return professorID;
    }

    public void setProfessorID(int professorID) {
        this.professorID = professorID;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
// Getters și setters
}