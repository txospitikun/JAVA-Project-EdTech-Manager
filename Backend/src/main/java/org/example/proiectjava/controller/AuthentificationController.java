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
                response.put("Privilege", EncryptionService.authenticateToken(responseToken));

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
        public ResponseEntity<String> create_professor(@RequestBody CreateProfessorRequest createProfessorRequest) {
            System.out.println(createProfessorRequest.getFirstName() + " " + createProfessorRequest.getLastName());
            int authenticationResponse = EncryptionService.authenticateToken(createProfessorRequest.getJWT());
            if (authenticationResponse == 3) {
                int professorID = RecordService.registerProfessor(createProfessorRequest);
                System.out.println(createProfessorRequest.getCourses() + " with prof id: " + professorID);
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


        @PutMapping("/update-professor/first_name")
        public ResponseEntity<String> updateProfessorFirstName(@RequestBody EditProfessorFirstNameRequest request) {
            int authenticationResponse = EncryptionService.authenticateToken(request.getJwt());
            if (authenticationResponse == 3) {
                boolean updateResponse = RecordService.updateProfessorFirstName(request);
                if (updateResponse) {
                    JSONObject response = new JSONObject();
                    response.put("Response", "successful");
                    return ResponseEntity.ok(response.toString());
                }
                return ResponseEntity.status(500).body("Failed to update first name.");
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @PutMapping("/update-professor/last_name")
        public ResponseEntity<String> updateProfessorLastName(@RequestBody EditProfessorLastNameRequest request) {
            int authenticationResponse = EncryptionService.authenticateToken(request.getJwt());
            if (authenticationResponse == 3) {
                boolean updateResponse = RecordService.updateProfessorLastName(request);
                if (updateResponse) {
                    JSONObject response = new JSONObject();
                    response.put("Response", "successful");
                    return ResponseEntity.ok(response.toString());
                }
                return ResponseEntity.status(500).body("Failed to update last name.");
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @PutMapping("/update-professor/rank")
        public ResponseEntity<String> updateProfessorRank(@RequestBody EditProfessorRankRequest request) {
            int authenticationResponse = EncryptionService.authenticateToken(request.getJwt());
            if (authenticationResponse == 3) {
                boolean updateResponse = RecordService.updateProfessorRank(request);
                if (updateResponse) {
                    JSONObject response = new JSONObject();
                    response.put("Response", "successful");
                    return ResponseEntity.ok(response.toString());
                }
                return ResponseEntity.status(500).body("Failed to update rank.");
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @PutMapping("/update-professor/username")
        public ResponseEntity<String> updateProfessorUsername(@RequestBody EditProfessorUsernameRequest request) {
            int authenticationResponse = EncryptionService.authenticateToken(request.getJwt());
            if (authenticationResponse == 3) {
                boolean updateResponse = RecordService.updateProfessorUsername(request);
                if (updateResponse) {
                    JSONObject response = new JSONObject();
                    response.put("Response", "successful");
                    return ResponseEntity.ok(response.toString());
                }
                return ResponseEntity.status(500).body("Failed to update username.");
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @PutMapping("/update-professor/courses")
        public ResponseEntity<String> updateProfessorCourses(@RequestBody EditProfessorCoursesRequest request) {
            int authenticationResponse = EncryptionService.authenticateToken(request.getJwt());
            if (authenticationResponse == 3) {
                boolean updateResponse = RecordService.updateProfessorCourses(request);
                if (updateResponse) {
                    JSONObject response = new JSONObject();
                    response.put("Response", "successful");
                    return ResponseEntity.ok(response.toString());
                }
                return ResponseEntity.status(500).body("Failed to update courses.");
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
            if (authenticationResponse == 3 || authenticationResponse == 2) {
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
            if (authenticationResponse == 3 || authenticationResponse == 2) {
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
            if (authenticationResponse == 3 || authenticationResponse == 2 || authenticationResponse == 1) {
                JSONArray coursesArray = RecordService.getAllCourses();
                JSONObject response = new JSONObject();
                response.put("courses", coursesArray);
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @PostMapping("/add_grade")
        public ResponseEntity<String> addGrade(@RequestBody AddGradeRequest addGradeRequest) {
            int authenticationResponse = EncryptionService.authenticateToken(addGradeRequest.getJWT());
            if (authenticationResponse == 3 || authenticationResponse == 2) {
                int gradeId = RecordService.addGrade(addGradeRequest);
                if (gradeId != -1) {
                    JSONObject response = new JSONObject();
                    response.put("Response", "successful");
                    response.put("gradeId", gradeId);
                    return ResponseEntity.ok(response.toString());
                }
                return ResponseEntity.status(500).body("Failed to add grade.");
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @PutMapping("/edit_grade")
        public ResponseEntity<String> editGrade(@RequestBody EditGradeRequest editGradeRequest) {
            int authenticationResponse = EncryptionService.authenticateToken(editGradeRequest.getJWT());
            if (authenticationResponse == 3 || authenticationResponse == 2) {
                boolean updateResponse = RecordService.editGrade(editGradeRequest);
                if (updateResponse) {
                    JSONObject response = new JSONObject();
                    response.put("Response", "successful");
                    return ResponseEntity.ok(response.toString());
                }
                return ResponseEntity.status(500).body("Failed to edit grade.");
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @DeleteMapping("/delete_grade")
        public ResponseEntity<String> deleteGrade(@RequestHeader("Authorization") String token, @RequestParam int gradeId) {
            int authenticationResponse = EncryptionService.authenticateToken(token);
            if (authenticationResponse == 3 || authenticationResponse == 2) {
                boolean deleteResponse = RecordService.deleteGrade(gradeId);
                if (deleteResponse) {
                    JSONObject response = new JSONObject();
                    response.put("Response", "successful");
                    return ResponseEntity.ok(response.toString());
                }
                return ResponseEntity.status(500).body("Failed to delete grade.");
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @GetMapping("/get_students")
        public ResponseEntity<String> getStudents(@RequestHeader("Authorization") String token) {
            int authenticationResponse = EncryptionService.authenticateToken(token);
            if (authenticationResponse == 3 || authenticationResponse == 2) {
                JSONArray studentsArray = RecordService.getAllStudents();
                JSONObject response = new JSONObject();
                response.put("students", studentsArray);
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @GetMapping("/get_grades")
        public ResponseEntity<String> getGrades(@RequestParam String nrMatricol, @RequestHeader("Authorization") String token) {
            int authenticationResponse = EncryptionService.authenticateToken(token);
            if (authenticationResponse == 3 || authenticationResponse == 2) {
                JSONArray gradesArray = RecordService.getGradesByNrMatricol(nrMatricol);
                JSONObject response = new JSONObject();
                response.put("grades", gradesArray);
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @GetMapping("/get_groups")
        public ResponseEntity<String> getGroups(@RequestHeader("Authorization") String token) {
            int authenticationResponse = EncryptionService.authenticateToken(token);
            if (authenticationResponse == 3 || authenticationResponse == 2) {
                JSONArray groupsArray = RecordService.getAllGroups();
                JSONObject response = new JSONObject();
                response.put("groups", groupsArray);
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @GetMapping("/get_students_by_group")
        public ResponseEntity<String> getStudentsByGroup(@RequestParam int groupId, @RequestHeader("Authorization") String token) {
            int authenticationResponse = EncryptionService.authenticateToken(token);
            if (authenticationResponse == 3 || authenticationResponse == 2) {
                JSONArray studentsArray = RecordService.getStudentsByGroup(groupId);
                JSONObject response = new JSONObject();
                response.put("students", studentsArray);
                return ResponseEntity.ok(response.toString());
            }
            return ResponseEntity.status(401).body("Invalid JWT.");
        }

        @GetMapping("/get_student_info")
        public ResponseEntity<String> getStudentInfo(@RequestHeader("Authorization") String token) {
            int authenticationResponse = EncryptionService.authenticateToken(token);
            if (authenticationResponse < 1) {
                return ResponseEntity.status(401).body("Invalid JWT.");
            }

            JSONObject studentInfo = RecordService.getStudentInfoByUserId(authenticationResponse);
            if (studentInfo != null) {
                return ResponseEntity.ok(studentInfo.toString());
            } else {
                return ResponseEntity.status(500).body("Failed to retrieve student info.");
            }
        }
    }
