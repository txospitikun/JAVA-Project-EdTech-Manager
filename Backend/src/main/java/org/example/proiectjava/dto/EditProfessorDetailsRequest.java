package org.example.proiectjava.dto;

public class EditProfessorDetailsRequest {
    private String jwt;
    private int professorID;
    private String firstname;
    private String lastname;
    private String rank;

    // Getters and setters
    public String getJWT() { return jwt; }
    public void setJWT(String jwt) { this.jwt = jwt; }

    public int getProfessorID() { return professorID; }
    public void setProfessorID(int professorID) { this.professorID = professorID; }

    public String getFirstName() { return firstname; }
    public void setFirstName(String firstname) { this.firstname = firstname; }

    public String getLastName() { return lastname; }
    public void setLastName(String lastname) { this.lastname = lastname; }

    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
}
