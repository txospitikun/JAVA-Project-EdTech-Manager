<<<<<<< Updated upstream
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
=======
package org.example.proiectjava.controller;

import org.example.proiectjava.dto.CreateProfessorRequest;
import org.example.proiectjava.dto.LoginRequest;
import org.example.proiectjava.dto.RegisterRequest;
import org.example.proiectjava.service.AuthService;
import org.example.proiectjava.service.EncryptionService;
import org.example.proiectjava.service.RecordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthentificationController {
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        boolean success = true;
        if (AuthService.authentificateUser(loginRequest)) {
            return ResponseEntity.ok("Login successful");
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
    public ResponseEntity<String> create_professor(@RequestBody CreateProfessorRequest createProfessorRequest) {
        System.out.println(createProfessorRequest.getFirstName() + " " + createProfessorRequest.getLastName());
        System.out.println("Courses received: " + createProfessorRequest.getCourses());

        int authenticationResponse = EncryptionService.authenticateToken(createProfessorRequest.getJWT());
        if (authenticationResponse == 3) {
            int professorID = RecordService.registerProfessor(createProfessorRequest);
            System.out.println("Courses to register: " + createProfessorRequest.getCourses());
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

    @PostMapping("/create_student")
    public ResponseEntity<String> createStudent(@RequestBody CreateStudentRequest createStudentRequest) {
        int authenticationResponse = EncryptionService.authenticateToken(createStudentRequest.getJWT());
        if (authenticationResponse == 3) {
            int studentId = RecordService.registerStudent(createStudentRequest);
            if (studentId != -1) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                response.put("studentId", studentId);
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to create student.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @PutMapping("/edit_student")
    public ResponseEntity<String> editStudent(@RequestBody EditStudentRequest editStudentRequest) {
        int authenticationResponse = EncryptionService.authenticateToken(editStudentRequest.getJWT());
        if (authenticationResponse == 3) {
            boolean updateResponse = RecordService.updateStudent(editStudentRequest);
            if (updateResponse) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to update student.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @DeleteMapping("/delete_student")
    public ResponseEntity<String> deleteStudent(@RequestHeader("Authorization") String token, @RequestParam int studentId) {
        int authenticationResponse = EncryptionService.authenticateToken(token);
        if (authenticationResponse == 3) {
            boolean deleteResponse = RecordService.deleteStudent(studentId);
            if (deleteResponse) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to delete student.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

// Group endpoints

    @PostMapping("/create_group")
    public ResponseEntity<String> createGroup(@RequestBody CreateGroupRequest createGroupRequest) {
        int authenticationResponse = EncryptionService.authenticateToken(createGroupRequest.getJWT());
        if (authenticationResponse == 3) {
            int groupId = RecordService.registerGroup(createGroupRequest);
            if (groupId != -1) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                response.put("groupId", groupId);
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to create group.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @PutMapping("/edit_group")
    public ResponseEntity<String> editGroup(@RequestBody EditGroupRequest editGroupRequest) {
        int authenticationResponse = EncryptionService.authenticateToken(editGroupRequest.getJWT());
        if (authenticationResponse == 3) {
            boolean updateResponse = RecordService.updateGroup(editGroupRequest);
            if (updateResponse) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to update group.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @DeleteMapping("/delete_group")
    public ResponseEntity<String> deleteGroup(@RequestHeader("Authorization") String token, @RequestParam int groupId) {
        int authenticationResponse = EncryptionService.authenticateToken(token);
        if (authenticationResponse == 3) {
            boolean deleteResponse = RecordService.deleteGroup(groupId);
            if (deleteResponse) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to delete group.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @PostMapping("/create_student_year")
    public ResponseEntity<String> createStudentYear(@RequestBody CreateStudentYearRequest createStudentYearRequest) {
        int authenticationResponse = EncryptionService.authenticateToken(createStudentYearRequest.getJWT());
        if (authenticationResponse == 3) {
            int studentYearId = RecordService.registerStudentYear(createStudentYearRequest);
            if (studentYearId != -1) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                response.put("studentYearId", studentYearId);
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to create student year.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @GetMapping("/professor_courses")
    public ResponseEntity<String> getProfessorCourses(@RequestParam int professorId, @RequestHeader("Authorization") String token) {
        int authenticationResponse = EncryptionService.authenticateToken(token);
        if (authenticationResponse == 3) {
            JSONArray coursesArray = RecordService.getCoursesByProfessor(professorId);
            JSONObject response = new JSONObject();
            response.put("courses", coursesArray);
            return ResponseEntity.ok(response.toString());
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @PostMapping("/assign_courses")
    public ResponseEntity<String> assignCoursesToProfessor(@RequestBody AssignCoursesRequest assignCoursesRequest) {
        int authenticationResponse = EncryptionService.authenticateToken(assignCoursesRequest.getJWT());
        if (authenticationResponse == 3) {
            boolean assignResponse = RecordService.assignCoursesToProfessor(assignCoursesRequest.getProfessorId(), assignCoursesRequest.getCourseIds());
            if (assignResponse) {
                JSONObject response = new JSONObject();
                response.put("Response", "successful");
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(500).body("Failed to assign courses to professor.");
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }

    @GetMapping("/get_professor")
    public ResponseEntity<String> getProfessorByUsername(@RequestHeader("Authorization") String token) {
        int authenticationResponse = EncryptionService.authenticateToken(token);
        if (authenticationResponse == 3) {
            JSONObject professor = RecordService.getProfessorByUsername(EncryptionService.getUsernameFromToken(token));
            if (professor.length() > 0) {
                return ResponseEntity.ok(professor.toString());
            } else {
                return ResponseEntity.status(404).body("Professor not found.");
            }
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }
    @GetMapping("/get_courses")
    public ResponseEntity<String> getCourses(@RequestHeader("Authorization") String token) {
        int authenticationResponse = EncryptionService.authenticateToken(token);
        if (authenticationResponse == 3) {
            JSONArray coursesArray = RecordService.getAllCourses();
            JSONObject response = new JSONObject();
            response.put("courses", coursesArray);
            return ResponseEntity.ok(response.toString());
        }
        return ResponseEntity.status(401).body("Invalid JWT.");
    }
>>>>>>> Stashed changes
}
>>>>>>> Stashed changes
