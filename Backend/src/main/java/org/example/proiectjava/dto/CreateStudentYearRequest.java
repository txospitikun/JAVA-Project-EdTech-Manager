package org.example.proiectjava.dto;

public class CreateStudentYearRequest {
    private String jwt;
    private int studentId;
    private int year;
    private int studyYear;
    private int groupId;

    // Getters and setters
    public String getJWT() { return jwt; }
    public void setJWT(String jwt) { this.jwt = jwt; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getStudyYear() { return studyYear; }
    public void setStudyYear(int studyYear) { this.studyYear = studyYear; }

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }

}
