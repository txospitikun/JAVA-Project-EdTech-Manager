package org.example.proiectjava.dto;

import jakarta.validation.constraints.NotBlank;
import org.json.JSONArray;

import java.util.List;

public class RegisterRequest
{
    @NotBlank
    public String jwt;
    @NotBlank
    public String username;
    @NotBlank
    public String password;
    @NotBlank
    public int privilege;

    public String getJWT() {return jwt; }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public int getPrivilege() { return privilege; }

}