package org.example.proiectjava.dto;

public class EditProfessorUserRequest {
    private String jwt;
    private int userId;
    private String username;

    // Getters and setters
    public String getJWT() { return jwt; }
    public void setJWT(String jwt) { this.jwt = jwt; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
