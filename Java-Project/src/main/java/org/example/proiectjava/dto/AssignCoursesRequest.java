package org.example.proiectjava.dto;

import java.util.List;

public class AssignCoursesRequest {
    private String jwt;
    private int professorId;
    private List<Integer> courseIds;

    // Getters and setters
    public String getJWT() {
        return jwt;
    }

    public void setJWT(String jwt) {
        this.jwt = jwt;
    }

    public int getProfessorId() {
        return professorId;
    }

    public void setProfessorId(int professorId) {
        this.professorId = professorId;
    }

    public List<Integer> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Integer> courseIds) {
        this.courseIds = courseIds;
    }
}
