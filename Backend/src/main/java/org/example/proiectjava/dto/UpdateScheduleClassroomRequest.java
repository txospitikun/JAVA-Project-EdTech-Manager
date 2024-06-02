package org.example.proiectjava.dto;

public class UpdateScheduleClassroomRequest {
    private String jwt;
    private int scheduleId;
    private int newClassroomId;
    private int linkId;

    // Getters and Setters
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public int getNewClassroomId() {
        return newClassroomId;
    }

    public void setNewClassroomId(int newClassroomId) {
        this.newClassroomId = newClassroomId;
    }

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }
}
