package org.example.proiectjava.controller;

import org.example.proiectjava.dto.RegisterRequest;
import org.example.proiectjava.service.EncryptionService;
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
        boolean authentificationResponse = EncryptionService.authenticateToken(registerRequest.getJWT());
        if(authentificationResponse)
        {
            System.out.println(registerRequest.getJWT());
            AuthService.registerUser(registerRequest);
            JSONObject response = new JSONObject();
            response.put("RegisterStatus", "succesful");
            return ResponseEntity.ok(response.toString());
        }
        else
        {
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

    }
}
