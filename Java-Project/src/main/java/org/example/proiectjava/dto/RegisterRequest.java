package org.example.proiectjava.dto;

import jakarta.validation.constraints.NotBlank;
import org.json.JSONArray;

import java.util.List;

public class RegisterRequest
{
    @NotBlank
    private String JWT;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private int privilege;
    @NotBlank
    private String name;

    private List<String> courses;

    public String getJWT() {return JWT; }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public int getPrivilege() { return privilege; }
    public String getName() { return name; }
    public List<String> getCourses() { return courses; };

}