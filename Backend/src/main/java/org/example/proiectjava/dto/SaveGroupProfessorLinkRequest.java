package org.example.proiectjava.dto;

public class SaveGroupProfessorLinkRequest {
    private String jwt;
    private int professorId;
    private int courseId;
    private int groupId;

    public SaveGroupProfessorLinkRequest() {}

    public SaveGroupProfessorLinkRequest(String jwt, int professorId, int courseId, int groupId) {
        this.jwt = jwt;
        this.professorId = professorId;
        this.courseId = courseId;
        this.groupId = groupId;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public int getProfessorId() {
        return professorId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
