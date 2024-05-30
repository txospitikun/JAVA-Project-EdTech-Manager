package org.example.proiectjava.controller;

import org.example.proiectjava.dto.CreateProfessorRequest;
import org.example.proiectjava.dto.RegisterRequest;
import org.example.proiectjava.service.EncryptionService;
import org.example.proiectjava.service.RecordService;
import org.json.*;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.example.proiectjava.dto.LoginRequest;
import org.example.proiectjava.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthentificationController {
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        String responseToken = AuthService.authentificateUser(loginRequest);
        if (!responseToken.isEmpty()) {
            JSONObject response = new JSONObject();
            response.put("JWT", responseToken);

            return ResponseEntity.ok(response.toString());
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest)
    {
        int authenticationResponse = EncryptionService.authenticateToken(registerRequest.getJWT());
        System.out.println(authenticationResponse);
        if(authenticationResponse == 3)
        {
            System.out.println(registerRequest.getJWT());
            AuthService.registerUser(registerRequest);
            JSONObject response = new JSONObject();
            response.put("Response", "successful");
            return ResponseEntity.ok(response.toString());
        }
        else
        {
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

    }

    @PostMapping("/create_professor")
    public ResponseEntity<String> create_professor(@RequestBody CreateProfessorRequest createProfessorRequest)
    {
        System.out.println(createProfessorRequest.getFirstName() + " " + createProfessorRequest.getLastName());
        int authenticationResponse = EncryptionService.authenticateToken(createProfessorRequest.getJWT());
        if(authenticationResponse == 3)
        {
            int professorID = RecordService.registerProfessor(createProfessorRequest);
            System.out.println(createProfessorRequest.getCourses() + "with prof id: " + professorID);
            RecordService.registerProfessorCourses(professorID, createProfessorRequest.getCourses());

            JSONObject response = new JSONObject();
            response.put("Response", "successful");
            return ResponseEntity.ok(response.toString());
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }
}
