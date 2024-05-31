package org.example.proiectjava.dto;

import java.util.Date;

public class EditGradeRequest {
    private String JWT;
    private int id;
    private int value;
    private Date notationDate;

    // Getters and setters
    public String getJWT() { return JWT; }
    public void setJWT(String JWT) { this.JWT = JWT; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getValue() { return value; }
    public void setValue(int value) { this.value = value; }

    public Date getNotationDate() { return notationDate; }
    public void setNotationDate(Date notationDate) { this.notationDate = notationDate; }
}