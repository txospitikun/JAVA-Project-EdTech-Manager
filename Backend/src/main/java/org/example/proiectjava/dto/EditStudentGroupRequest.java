package org.example.proiectjava.dto;

public class EditStudentGroupRequest {
    private String jwt;
    private int studentId;
    private String group;

    public EditStudentGroupRequest(String jwt, int studentId, String group) {
        this.jwt = jwt;
        this.studentId = studentId;
        this.group = group;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
