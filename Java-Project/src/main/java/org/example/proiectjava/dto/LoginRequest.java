package org.example.proiectjava.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank
    public String username;

    @NotBlank
    public String password;

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

}