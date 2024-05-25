package org.example.proiectjava.controller;

import org.example.proiectjava.dto.RegisterRequest;
import org.example.proiectjava.service.EncryptionService;
import org.json.*;

import com.fasterxml.jackson.databind.util.JSONPObject;
import org.example.proiectjava.dto.LoginRequest;
import org.example.proiectjava.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthentificationController {
    private static Logger logger = LoggerFactory.getLogger(AuthentificationController.class);

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
        logger.info(registerRequest.getJWT());
        boolean authentificationResponse = EncryptionService.authenticateToken(registerRequest.getJWT());
        if(authentificationResponse)
        {
            System.out.println(registerRequest.getJWT());
            int registerStatus = AuthService.registerUser(registerRequest);
            JSONObject response = new JSONObject();
            switch(registerStatus)
            {
                case 1:
                    response.put("RegisterStatus", "1001");
                    break;
                case 2:
                    response.put("RegisterStatus", "1002");
                    break;
                default:
                    response.put("RegisterStatus", "0");
            }
            return ResponseEntity.ok(response.toString());
        }
        else
        {
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

    }
}
