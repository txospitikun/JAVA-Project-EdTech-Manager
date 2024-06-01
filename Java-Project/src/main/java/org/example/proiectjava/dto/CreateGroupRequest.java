package org.example.proiectjava.dto;

public class CreateGroupRequest {
    private String JWT;
    private String groupName;

    // Getters and setters
    public String getJWT() { return JWT; }
    public void setJWT(String JWT) { this.JWT = JWT; }

    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}