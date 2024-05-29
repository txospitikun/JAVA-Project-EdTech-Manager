package org.example.proiectjava.dto;

public class CreateStudentRequest {
    private String jwt;
    private String nrMatricol;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private int privilege;

    // Getters and setters
    public String getJWT() { return jwt; }
    public void setJWT(String jwt) { this.jwt = jwt; }

    public String getNrMatricol() { return nrMatricol; }
    public void setNrMatricol(String nrMatricol) { this.nrMatricol = nrMatricol; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getPrivilege() { return privilege; }
    public void setPrivilege(int privilege) { this.privilege = privilege; }
}
