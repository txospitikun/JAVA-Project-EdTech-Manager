package org.example.proiectjava.dto;

public class CreateCourseRequest {
    private String jwt;
    private String courseTitle;
    private int year;
    private int semester;
    private int credits;

    public CreateCourseRequest(String jwt, String courseTitle, int year, int semester, int credits) {
        this.jwt = jwt;
        this.courseTitle = courseTitle;
        this.year = year;
        this.semester = semester;
        this.credits = credits;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }
}
