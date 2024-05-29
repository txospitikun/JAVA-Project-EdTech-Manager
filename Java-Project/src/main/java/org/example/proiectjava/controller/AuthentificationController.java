package org.example.proiectjava.controller;

import org.example.proiectjava.dto.*;
import org.example.proiectjava.service.EncryptionService;
import org.example.proiectjava.service.RecordService;
import org.json.*;

import org.example.proiectjava.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/get_professors")
    public ResponseEntity<String> getProfessors(@RequestHeader("Authorization") String token) {
        int authenticationResponse = EncryptionService.authenticateToken(token);
        if (authenticationResponse == 3) {
            JSONArray professorsArray = RecordService.getAllProfessors();
            JSONObject response = new JSONObject();
            response.put("professors", professorsArray);
            return ResponseEntity.ok(response.toString());
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @PutMapping("/edit_professor")
    public ResponseEntity<String> editProfessor(@RequestBody EditProfessorRequest editProfessorRequest) {
        int authenticationResponse = EncryptionService.authenticateToken(editProfessorRequest.getJWT());
        if (authenticationResponse == 3) {
            boolean updateResponse = RecordService.updateProfessor(editProfessorRequest);
            if (updateResponse) {
                System.out.println("Updated professor ID: " + editProfessorRequest.getProfessorID());
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                return ResponseEntity.ok(response.toString());
            } else {
                return ResponseEntity.status(500).body("Failed to update professor.");
            }
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @PostMapping("/create_course")
    public ResponseEntity<String> createCourse(@RequestBody CreateCourseRequest createCourseRequest) {
        int authenticationResponse = EncryptionService.authenticateToken(createCourseRequest.getJWT());
        if (authenticationResponse == 3) {
            int courseId = RecordService.registerCourse(createCourseRequest);
            if (courseId != -1) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                response.put("courseId", courseId);
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to create course.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @PutMapping("/edit_course")
    public ResponseEntity<String> editCourse(@RequestBody EditCourseRequest editCourseRequest) {
        int authenticationResponse = EncryptionService.authenticateToken(editCourseRequest.getJWT());
        if (authenticationResponse == 3) {
            boolean updateResponse = RecordService.updateCourse(editCourseRequest);
            if (updateResponse) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to update course.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @DeleteMapping("/delete_course")
    public ResponseEntity<String> deleteCourse(@RequestHeader("Authorization") String token, @RequestParam int courseId) {
        int authenticationResponse = EncryptionService.authenticateToken(token);
        if (authenticationResponse == 3) {
            boolean deleteResponse = RecordService.deleteCourse(courseId);
            if (deleteResponse) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to delete course.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

}
