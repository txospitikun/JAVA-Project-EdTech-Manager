package org.example.proiectjava.dto;

import java.util.Date;

public class AddGradeRequest {
    private String JWT;
    private String nrMatricol;
    private int idCourse;
    private int value;
    private Date notationDate;

    // Getters and setters
    public String getJWT() { return JWT; }
    public void setJWT(String JWT) { this.JWT = JWT; }

    public String getNrMatricol() { return nrMatricol; }
    public void setNrMatricol(String nrMatricol) { this.nrMatricol = nrMatricol; }

    public int getIdCourse() { return idCourse; }
    public void setIdCourse(int idCourse) { this.idCourse = idCourse; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public Date getNotationDate() { return notationDate; }
    public void setNotationDate(Date notationDate) { this.notationDate = notationDate; }
}