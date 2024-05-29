package org.example.proiectjava.dto;

public class EditStudentRequest {
    private String JWT;
    private int studentId;
    private String nrMatricol;
    private String firstName;
    private String lastName;

    // Getters and setters
    public String getJWT() { return JWT; }
    public void setJWT(String JWT) { this.JWT = JWT; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getNrMatricol() { return nrMatricol; }
    public void setNrMatricol(String nrMatricol) { this.nrMatricol = nrMatricol; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}
