package org.example.proiectjava.dto;

public class EditCourseRequest {
    private String JWT;
    private int courseId;
    private String courseTitle;
    private int year;
    private int semester;
    private int credits;

    // Getters and setters
    public String getJWT() { return JWT; }
    public void setJWT(String JWT) { this.JWT = JWT; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getCourseTitle() { return courseTitle; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public int getSemester() { return semester; }
    public void setSemester(int semester) { this.semester = semester; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
}