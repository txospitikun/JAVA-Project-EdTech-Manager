package org.example.proiectjava.dto;

public class StudentYear {
    private int studentId;
    private int year;
    private int studyYear;
    private int groupId;

    public StudentYear(int studentId, int year, int studyYear, int groupId) {
        this.studentId = studentId;
        this.year = year;
        this.studyYear = studyYear;
        this.groupId = groupId;
    }

    // Getters and setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(int studyYear) {
        this.studyYear = studyYear;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }
}
