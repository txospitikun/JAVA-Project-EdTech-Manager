<<<<<<< Updated upstream
package org.example.proiectjava.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class CreateProfessorRequest {
    @NotBlank
    public String jwt;
    @NotBlank
    public String firstname;
    @NotBlank
    public String lastname;
    @NotBlank
    public String rank;
    @NotBlank
    public String username;
    @NotBlank
    public String password;
    @NotBlank
    public List<String> courses;

    public String getJWT() {
        return jwt;
    }

    public String getUsername() {
        return username;
    }

    public String getRank() {
        return rank;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return this.firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public List<String> getCourses() {
        return courses;
    }
}
=======
package org.example.proiectjava.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class CreateProfessorRequest {
    @NotBlank
    public String jwt;
    @NotBlank
    public String firstname;
    @NotBlank
    public String lastname;
    @NotBlank
    public String rank;
    @NotBlank
    public String username;
    @NotBlank
    public String password;
    @NotBlank
    private List<String> courses;

    public String getJWT() {
        return jwt;
    }

    public String getUsername() {
        return username;
    }

    public String getRank() {
        return rank;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return this.firstname;
    }

    public String getLastName() {
        return lastname;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }
}
>>>>>>> Stashed changes
