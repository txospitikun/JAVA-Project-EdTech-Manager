package org.example.proiectjava.service;

import org.example.proiectjava.database.DatabaseConfig;
import org.example.proiectjava.dto.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class RecordService {
    public static int registerProfessor(CreateProfessorRequest createProfessorRequest)
    {
        int registeredUserID = -1;
        if((registeredUserID = AuthService.registerUser(createProfessorRequest.getUsername(), EncryptionService.encryptSHA256(createProfessorRequest.getPassword()), 2)) == -1)
        {
            return -1;
        }

        String query = "INSERT INTO Professors (first_name, last_name, rank, user_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, createProfessorRequest.getFirstName());
                preparedStatement.setString(2, createProfessorRequest.getLastName());
                preparedStatement.setString(3, createProfessorRequest.getRank());
                preparedStatement.setInt(4, registeredUserID);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1); // Assuming the generated key is in the first column
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return -1;
    }

    public static int registerProfessorCourses(int professorID, List<String> courses) {
        if (professorID == -1) {
            return -1;
        }

        for (String courseTitle : courses) {
            int foundCourseID = -1;
            String query = "SELECT ID FROM Courses WHERE course_title = ?";
            try (Connection connection = DatabaseConfig.getConnection()) {
                if (connection != null) {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, courseTitle);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            foundCourseID = resultSet.getInt("ID");
                            System.out.println("Found course with ID: " + foundCourseID);
                        } else {
                            foundCourseID = -1;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
            }

            if (foundCourseID == -1) {
                continue;
            }

            String query2 = "INSERT INTO Didactic (id_professor, id_course) VALUES (?, ?)";
            try (Connection connection = DatabaseConfig.getConnection()) {
                if (connection != null) {
                    PreparedStatement preparedStatement = connection.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);
                    preparedStatement.setInt(1, professorID);
                    preparedStatement.setInt(2, foundCourseID);

                    int affectedRows = preparedStatement.executeUpdate();
                    if (affectedRows > 0) {
                        try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                System.out.println("Inserted Didactic record with ID: " + generatedKeys.getInt(1));
                            }
                        }
                    } else {
                        return -1;
                    }
                }
            } catch (Exception e) {
                System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
                return -1;
            }
        }
        return 1;
    }


    public static int getCourseIdByName(String courseTitle) {
        String query = "SELECT id FROM Courses WHERE course_title = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, courseTitle);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return -1;
    }

    public static int getProfessorIdByName(String professorName) {
        String query = "SELECT id FROM Professors WHERE CONCAT(first_name, ' ', last_name) = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, professorName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return -1;
    }

    public static boolean saveGroupProfessorLink(List<SaveGroupProfessorLinkRequest> requests) {
        String query = "INSERT INTO GroupProfessorLink (prof_id, course_id, group_id) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                for (SaveGroupProfessorLinkRequest request : requests) {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, request.getProfessorId());
                    preparedStatement.setInt(2, request.getCourseId());
                    preparedStatement.setInt(3, request.getGroupId());
                    preparedStatement.executeUpdate();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static JSONArray getAllProfessors() {
        JSONArray professorsArray = new JSONArray();
        String query = "SELECT p.id, p.first_name, p.last_name, p.rank, u.id AS user_id, u.username, " +
                "ARRAY_AGG(c.course_title) AS courses " +
                "FROM professors p " +
                "JOIN users u ON p.user_id = u.id " +
                "LEFT JOIN didactic d ON p.id = d.id_professor " +
                "LEFT JOIN courses c ON d.id_course = c.id " +
                "GROUP BY p.id, p.first_name, p.last_name, p.rank, u.id, u.username";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                JSONObject professor = new JSONObject();
                professor.put("id", rs.getInt("id"));
                professor.put("first_name", rs.getString("first_name"));
                professor.put("last_name", rs.getString("last_name"));
                professor.put("rank", rs.getString("rank"));
                professor.put("user_id", rs.getInt("user_id"));
                professor.put("username", rs.getString("username"));

                Array coursesArray = rs.getArray("courses");
                if (coursesArray != null) {
                    String[] courses = (String[]) coursesArray.getArray();
                    professor.put("courses", courses);
                } else {
                    professor.put("courses", new String[0]);
                }

                professorsArray.put(professor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return professorsArray;
    }



    public static boolean updateProfessorFirstName(EditProfessorFirstNameRequest request) {
        String query = "UPDATE Professors SET first_name = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, request.getFirstName());
                preparedStatement.setInt(2, request.getProfessorID());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateProfessorLastName(EditProfessorLastNameRequest request) {
        String query = "UPDATE Professors SET last_name = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, request.getLastName());
                preparedStatement.setInt(2, request.getProfessorID());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateProfessorRank(EditProfessorRankRequest request) {
        String query = "UPDATE Professors SET rank = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, request.getRank());
                preparedStatement.setInt(2, request.getProfessorID());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateProfessorUsername(EditProfessorUsernameRequest request) {
        String query = "UPDATE Users SET username = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, request.getUsername());
                preparedStatement.setInt(2, request.getUserId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateProfessorCourses(EditProfessorCoursesRequest request) {
        int professorId = request.getProfessorID();
        List<String> courses = request.getCourses();

        // Ștergeți cursurile existente
        String deleteQuery = "DELETE FROM Didactic WHERE id_professor = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery);
                deleteStatement.setInt(1, professorId);
                deleteStatement.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
            return false;
        }

        // Adăugați cursurile noi
        for (String courseTitle : courses) {
            int foundCourseID = -1;
            String query = "SELECT ID FROM Courses WHERE course_title = ?";
            try (Connection connection = DatabaseConfig.getConnection()) {
                if (connection != null) {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, courseTitle);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            foundCourseID = resultSet.getInt("ID");
                        } else {
                            foundCourseID = -1;
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
                return false;
            }

            if (foundCourseID == -1) {
                continue;
            }

            String insertQuery = "INSERT INTO Didactic (id_professor, id_course) VALUES (?, ?)";
            try (Connection connection = DatabaseConfig.getConnection()) {
                if (connection != null) {
                    PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                    insertStatement.setInt(1, professorId);
                    insertStatement.setInt(2, foundCourseID);
                    insertStatement.executeUpdate();
                }
            } catch (Exception e) {
                System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
                return false;
            }
        }
        return true;
    }



    private static void updateProfessorCourses(int professorID, List<String> courses) {
        // Delete existing courses for the professor
        String deleteCoursesQuery = "DELETE FROM Didactic WHERE id_professor = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(deleteCoursesQuery);
                preparedStatement.setInt(1, professorID);
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred while deleting courses: " + e.getMessage());
        }

        // Insert new courses for the professor
        for (String course : courses) {
            String query = "SELECT ID FROM Courses WHERE course_title = ?";
            int foundCourseID = -1;
            try (Connection connection = DatabaseConfig.getConnection()) {
                if (connection != null) {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, course);

                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            foundCourseID = resultSet.getInt("ID");
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
            }

            if (foundCourseID != -1) {
                String insertCourseQuery = "INSERT INTO Didactic (id_professor, id_course) VALUES (?, ?)";
                try (Connection connection = DatabaseConfig.getConnection()) {
                    if (connection != null) {
                        PreparedStatement preparedStatement = connection.prepareStatement(insertCourseQuery, Statement.RETURN_GENERATED_KEYS);
                        preparedStatement.setInt(1, professorID);
                        preparedStatement.setInt(2, foundCourseID);

                        int affectedRows = preparedStatement.executeUpdate();
                        if (affectedRows > 0) {
                            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    System.out.println("Inserted Didactic record with ID: " + generatedKeys.getInt(1));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("An unexpected SQL exception has occurred while inserting courses: " + e.getMessage());
                }
            }
        }
    }
    public static JSONArray getAllCourses() {
        JSONArray coursesArray = new JSONArray();
        String query = "SELECT * FROM Courses ORDER BY YEAR, SEMESTER ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                JSONObject course = new JSONObject();
                course.put("id", rs.getInt("ID")); // Adăugăm ID-ul în răspuns
                course.put("courseTitle", rs.getString("COURSE_TITLE"));
                course.put("year", rs.getInt("YEAR"));
                course.put("semester", rs.getInt("SEMESTER"));
                course.put("credits", rs.getInt("CREDITS"));
                coursesArray.put(course);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coursesArray;
    }

    public static int registerCourse(CreateCourseRequest createCourseRequest) {
        String query = "INSERT INTO Courses (course_title, year, semester, credits) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, createCourseRequest.getCourseTitle());
                preparedStatement.setInt(2, createCourseRequest.getYear());
                preparedStatement.setInt(3, createCourseRequest.getSemester());
                preparedStatement.setInt(4, createCourseRequest.getCredits());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1); // Assuming the generated key is in the first column
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return -1;
    }

    public static boolean updateCourseTitle(EditCourseTitleRequest request) {
        String query = "UPDATE Courses SET course_title = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, request.getCourseTitle());
                preparedStatement.setInt(2, request.getCourseId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateCourseYear(EditCourseYearRequest request) {
        String query = "UPDATE Courses SET year = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, request.getYear());
                preparedStatement.setInt(2, request.getCourseId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateCourseSemester(EditCourseSemesterRequest request) {
        String query = "UPDATE Courses SET semester = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, request.getSemester());
                preparedStatement.setInt(2, request.getCourseId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateCourseCredits(EditCourseCreditsRequest request) {
        String query = "UPDATE Courses SET credits = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, request.getCredits());
                preparedStatement.setInt(2, request.getCourseId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }


    public static boolean deleteCourse(int courseId) {
        String query = "DELETE FROM Courses WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, courseId);

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static int registerStudent(CreateStudentRequest createStudentRequest) {
        int registeredUserID = -1;
        if ((registeredUserID = AuthService.registerUser(createStudentRequest.getUsername(), EncryptionService.encryptSHA256(createStudentRequest.getPassword()), 1)) == -1) {
            return -1;
        }

        String nrMatricol = generateNrMatricol(registeredUserID);

        String query = "INSERT INTO Students (nr_matricol, first_name, last_name, user_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, nrMatricol);
                preparedStatement.setString(2, createStudentRequest.getFirstName());
                preparedStatement.setString(3, createStudentRequest.getLastName());
                preparedStatement.setInt(4, registeredUserID);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int studentId = generatedKeys.getInt(1);
                            registerStudentYear(new CreateStudentYearRequest(studentId, LocalDate.now().getYear(), extractStudyYearFromGroupName(createStudentRequest.getGroup()), findGroupIdByName(createStudentRequest.getGroup())));
                            return studentId;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return -1;
    }

    public static boolean updateStudent(EditStudentRequest editStudentRequest) {
        String query = "UPDATE Students SET nr_matricol = ?, first_name = ?, last_name = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, editStudentRequest.getNrMatricol());
                preparedStatement.setString(2, editStudentRequest.getFirstName());
                preparedStatement.setString(3, editStudentRequest.getLastName());
                preparedStatement.setInt(4, editStudentRequest.getStudentId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteStudent(int studentId) {
        String query = "DELETE FROM Students WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, studentId);

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }



    public static boolean updateGroup(EditGroupRequest editGroupRequest) {
        String query = "UPDATE Groups SET group_name = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, editGroupRequest.getGroupName());
                preparedStatement.setInt(2, editGroupRequest.getGroupId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteGroup(int groupId) {
        String query = "DELETE FROM Groups WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, groupId);

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static int registerStudentYear(CreateStudentYearRequest request) {
        String query = "INSERT INTO StudentYears (id_student, year, study_year, group_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, request.getStudentId());
                preparedStatement.setInt(2, request.getYear());
                preparedStatement.setInt(3, request.getStudyYear());
                preparedStatement.setInt(4, request.getGroupId());
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return 0;
    }
    public static JSONArray getCurrentYearStudents() {
        JSONArray studentsArray = new JSONArray();
        String query = "SELECT s.id AS studentId, u.id AS userId, sy.group_id AS groupId, s.nr_matricol, s.first_name, s.last_name, u.username, g.group_name " +
                "FROM Students s " +
                "JOIN Users u ON s.user_id = u.id " +
                "JOIN StudentYears sy ON s.id = sy.id_student " +
                "JOIN Groups g ON sy.group_id = g.id " +
                "WHERE sy.year = EXTRACT(YEAR FROM CURRENT_DATE)::INTEGER";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                JSONObject student = new JSONObject();
                student.put("studentId", resultSet.getInt("studentId"));
                student.put("userId", resultSet.getInt("userId"));
                student.put("groupId", resultSet.getInt("groupId"));
                student.put("nrMatricol", resultSet.getString("nr_matricol"));
                student.put("firstName", resultSet.getString("first_name"));
                student.put("lastName", resultSet.getString("last_name"));
                student.put("username", resultSet.getString("username"));
                student.put("group", resultSet.getString("group_name"));

                studentsArray.put(student);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return studentsArray;
    }


    public static JSONArray getCoursesByProfessor(int professorId) {
        JSONArray coursesArray = new JSONArray();
        String query = "SELECT c.id, c.course_title, c.year, c.semester, c.credits " +
                "FROM Courses c " +
                "JOIN Didactic d ON c.id = d.id_course " +
                "WHERE d.id_professor = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, professorId);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    JSONObject course = new JSONObject();
                    course.put("id", rs.getInt("id"));
                    course.put("courseTitle", rs.getString("course_title"));
                    course.put("year", rs.getInt("year"));
                    course.put("semester", rs.getInt("semester"));
                    course.put("credits", rs.getInt("credits"));
                    coursesArray.put(course);
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return coursesArray;
    }

    public static boolean assignCoursesToProfessor(int professorId, List<Integer> courseIds) {
        String deleteQuery = "DELETE FROM Didactic WHERE id_professor = ?";
        String insertQuery = "INSERT INTO Didactic (id_professor, id_course) VALUES (?, ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                // Start transaction
                connection.setAutoCommit(false);

                // Delete existing courses
                try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                    deleteStatement.setInt(1, professorId);
                    deleteStatement.executeUpdate();
                }

                // Insert new courses
                try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                    for (int courseId : courseIds) {
                        insertStatement.setInt(1, professorId);
                        insertStatement.setInt(2, courseId);
                        insertStatement.addBatch();
                    }
                    insertStatement.executeBatch();
                }

                // Commit transaction
                connection.commit();
                return true;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static JSONObject getProfessorByUsername(String username) {
        JSONObject professor = new JSONObject();
        String query = "SELECT p.id, p.first_name, p.last_name, p.rank, p.user_id, u.username " +
                "FROM Professors p " +
                "JOIN Users u ON p.user_id = u.id " +
                "WHERE u.username = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    professor.put("id", rs.getInt("id"));
                    professor.put("firstName", rs.getString("first_name"));
                    professor.put("lastName", rs.getString("last_name"));
                    professor.put("rank", rs.getString("rank"));
                    professor.put("userId", rs.getInt("user_id"));
                    professor.put("username", rs.getString("username"));
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return professor;
    }

    public static int addGrade(AddGradeRequest addGradeRequest) {
        String query = "INSERT INTO Grades (nr_matricol, id_prof, id_course, value, notation_date) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, addGradeRequest.getNrMatricol());
                preparedStatement.setInt(2, RecordService.getProfessorIdByUserId(EncryptionService.getUserIDFromToken(addGradeRequest.getJWT())));
                preparedStatement.setInt(3, addGradeRequest.getIdCourse());
                preparedStatement.setInt(4, addGradeRequest.getValue());
                preparedStatement.setDate(5, new java.sql.Date(addGradeRequest.getNotationDate().getTime()));

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1); // Assuming the generated key is in the first column
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return -1;
    }

    public static boolean editGrade(EditGradeRequest editGradeRequest) {
        String query = "UPDATE Grades SET value = ?, notation_date = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, editGradeRequest.getValue());
                preparedStatement.setDate(2, new java.sql.Date(editGradeRequest.getNotationDate().getTime()));
                preparedStatement.setInt(3, editGradeRequest.getId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean deleteGrade(int gradeId) {
        String query = "DELETE FROM Grades WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, gradeId);

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static JSONArray getAllStudents() {
        JSONArray studentsArray = new JSONArray();
        String query = "SELECT id, nr_matricol, first_name, last_name FROM Students";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    JSONObject student = new JSONObject();
                    student.put("id", rs.getInt("id"));
                    student.put("nrMatricol", rs.getString("nr_matricol"));
                    student.put("firstName", rs.getString("first_name"));
                    student.put("lastName", rs.getString("last_name"));
                    studentsArray.put(student);
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return studentsArray;
    }

    public static JSONArray getGradesByNrMatricol(String nrMatricol) {
        JSONArray gradesArray = new JSONArray();
        String query = "SELECT g.id, g.nr_matricol, g.id_course, g.value, g.notation_date, c.course_title, " +
                "p.first_name AS professor_first_name, p.last_name AS professor_last_name " +
                "FROM Grades g " +
                "JOIN Courses c ON g.id_course = c.id " +
                "JOIN Professors p ON g.id_prof = p.id " +
                "WHERE g.nr_matricol = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, nrMatricol);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    JSONObject grade = new JSONObject();
                    grade.put("id", rs.getInt("id"));
                    grade.put("nrMatricol", rs.getString("nr_matricol"));
                    grade.put("idCourse", rs.getInt("id_course"));
                    grade.put("value", rs.getInt("value"));
                    grade.put("notationDate", rs.getDate("notation_date"));
                    grade.put("courseTitle", rs.getString("course_title"));
                    grade.put("professorFirstName", rs.getString("professor_first_name"));
                    grade.put("professorLastName", rs.getString("professor_last_name"));
                    gradesArray.put(grade);
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return gradesArray;
    }


    public static JSONArray getAllGroups() {
        JSONArray groupsArray = new JSONArray();
        String query = "SELECT id, group_name FROM Groups WHERE year = EXTRACT(YEAR FROM CURRENT_DATE)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    JSONObject group = new JSONObject();
                    group.put("id", rs.getInt("id"));
                    group.put("groupName", rs.getString("group_name"));
                    groupsArray.put(group);
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return groupsArray;
    }


    private static int extractStudyYearFromGroupName(String groupName) {
        return Character.getNumericValue(groupName.charAt(0));
    }

    private static String generateNrMatricol(int userId) {
        int currentYear = LocalDate.now().getYear();
        return currentYear + String.format("%04d", userId);
    }
    public static JSONArray getStudentsByGroup(int groupId) {
        JSONArray studentsArray = new JSONArray();
        String query = "SELECT s.id, s.nr_matricol, s.first_name, s.last_name " +
                "FROM Students s " +
                "JOIN StudentYears sy ON s.id = sy.id_student " +
                "WHERE sy.group_id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, groupId);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    JSONObject student = new JSONObject();
                    student.put("id", rs.getInt("id"));
                    student.put("nrMatricol", rs.getString("nr_matricol"));
                    student.put("firstName", rs.getString("first_name"));
                    student.put("lastName", rs.getString("last_name"));
                    studentsArray.put(student);
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return studentsArray;
    }

    public static JSONObject getStudentInfoByUserId(int userId) {
        JSONObject studentInfo = new JSONObject();
        String studentQuery = "SELECT * FROM Students WHERE user_id = ?";
        String studentYearQuery = "SELECT * FROM StudentYears WHERE id_student = ?";
        String groupQuery = "SELECT group_name FROM Groups WHERE id = ?";

        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                // Fetch student data
                PreparedStatement studentStmt = connection.prepareStatement(studentQuery);
                studentStmt.setInt(1, userId);
                ResultSet studentRs = studentStmt.executeQuery();
                if (studentRs.next()) {
                    studentInfo.put("id", studentRs.getInt("id"));
                    studentInfo.put("nrMatricol", studentRs.getString("nr_matricol"));
                    studentInfo.put("firstName", studentRs.getString("first_name"));
                    studentInfo.put("lastName", studentRs.getString("last_name"));

                    int studentId = studentRs.getInt("id");

                    // Fetch student year data
                    PreparedStatement studentYearStmt = connection.prepareStatement(studentYearQuery);
                    studentYearStmt.setInt(1, studentId);
                    ResultSet studentYearRs = studentYearStmt.executeQuery();
                    if (studentYearRs.next()) {
                        studentInfo.put("year", studentYearRs.getInt("year"));
                        studentInfo.put("studyYear", studentYearRs.getInt("study_year"));
                        int groupId = studentYearRs.getInt("group_id");

                        // Fetch group data
                        PreparedStatement groupStmt = connection.prepareStatement(groupQuery);
                        groupStmt.setInt(1, groupId);
                        ResultSet groupRs = groupStmt.executeQuery();
                        if (groupRs.next()) {
                            studentInfo.put("groupName", groupRs.getString("group_name"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return studentInfo;
    }
    public static boolean updateStudentFirstName(EditStudentFirstNameRequest request) {
        String query = "UPDATE Students SET first_name = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, request.getFirstName());
                preparedStatement.setInt(2, request.getStudentId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateStudentLastName(EditStudentLastNameRequest request) {
        String query = "UPDATE Students SET last_name = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, request.getLastName());
                preparedStatement.setInt(2, request.getStudentId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateStudentUsername(EditStudentUsernameRequest request) {
        String query = "UPDATE Users SET username = ? WHERE id = (SELECT user_id FROM Students WHERE id = ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, request.getUsername());
                preparedStatement.setInt(2, request.getStudentId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static boolean updateStudentGroup(EditStudentGroupRequest request) {
        String query = "UPDATE StudentYears SET group_id = ?, study_year = ? WHERE id_student = ? AND year = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                int newGroupId = findGroupIdByName(request.getGroup());
                if (newGroupId == -1) {
                    System.err.println("Group not found: " + request.getGroup());
                    return false;
                }
                int newStudyYear = Character.getNumericValue(request.getGroup().charAt(0));
                int currentYear = LocalDate.now().getYear();

                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, newGroupId);
                preparedStatement.setInt(2, newStudyYear);
                preparedStatement.setInt(3, request.getStudentId());
                preparedStatement.setInt(4, currentYear);

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }


    public static boolean createAnnouncement(CreateAnnouncementRequest request) {
        String query = "INSERT INTO Announcements (announcement_title, announcement_content, upload_date) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, request.getAnnouncementTitle());
                preparedStatement.setString(2, request.getAnnouncementContent());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
    }

    public static JSONArray getAllAnnouncements() {
        JSONArray announcementsArray = new JSONArray();
        String query = "SELECT id, announcement_title, announcement_content, upload_date FROM Announcements ORDER BY id DESC";

        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                JSONObject announcement = new JSONObject();
                announcement.put("id", resultSet.getInt("id"));
                announcement.put("announcementTitle", resultSet.getString("announcement_title"));
                announcement.put("announcementContent", resultSet.getString("announcement_content"));
                announcement.put("uploadDate", resultSet.getDate("upload_date").toString());

                announcementsArray.put(announcement);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return announcementsArray;
    }
    public static boolean deleteAnnouncement(int id) {
        String query = "DELETE FROM Announcements WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, id);

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean updateAnnouncement(EditAnnouncementRequest request) {
        String query = "UPDATE Announcements SET announcement_title = ?, announcement_content = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, request.getAnnouncementTitle());
                preparedStatement.setString(2, request.getAnnouncementContent());
                preparedStatement.setInt(3, request.getId());

                int affectedRows = preparedStatement.executeUpdate();
                return affectedRows > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static int getProfessorIdByUserId(int userId) {
        String query = "SELECT id FROM Professors WHERE user_id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, userId);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int registerGroup(CreateGroupRequest createGroupRequest) {
        String query = "INSERT INTO Groups (group_name) VALUES (?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, createGroupRequest.getGroupName());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return -1;
    }



    public static JSONArray getCoursesForYear(int year) {
        JSONArray coursesArray = new JSONArray();
        String query = "SELECT id, course_title FROM Courses WHERE year = ?";

        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, year);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    JSONObject course = new JSONObject();
                    course.put("id", rs.getInt("id"));
                    course.put("courseTitle", rs.getString("course_title"));
                    coursesArray.put(course);
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return coursesArray;
    }

    public static JSONArray getGroupCourseProfessors(int groupId) {
        JSONArray groupCourseProfessors = new JSONArray();
        String query = "SELECT course_id, prof_id FROM GroupProfessorLink WHERE group_id = ?";

        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, groupId);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    JSONObject groupCourseProfessor = new JSONObject();
                    groupCourseProfessor.put("courseId", rs.getInt("course_id"));
                    groupCourseProfessor.put("profId", rs.getInt("prof_id"));
                    groupCourseProfessors.put(groupCourseProfessor);
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return groupCourseProfessors;
    }

    public static String getProfessorNameById(int profId) {
        String query = "SELECT first_name, last_name FROM Professors WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, profId);
                ResultSet rs = preparedStatement.executeQuery();
                if (rs.next()) {
                    return rs.getString("first_name") + " " + rs.getString("last_name");
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return null;
    }

    public static JSONArray getProfessorsForCourse(int courseId) {
        JSONArray professorsArray = new JSONArray();
        String query = "SELECT p.id, CONCAT(p.first_name, ' ', p.last_name) AS name " +
                "FROM Professors p " +
                "JOIN Didactic d ON p.id = d.id_professor " +
                "WHERE d.id_course = ?";

        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, courseId);
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    JSONObject professor = new JSONObject();
                    professor.put("id", rs.getInt("id"));
                    professor.put("name", rs.getString("name"));
                    professorsArray.put(professor);
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return professorsArray;
    }

    public static int findGroupIdByName(String groupName) {
        String query = "SELECT id FROM Groups WHERE group_name = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, groupName);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return -1;
    }
    public static boolean saveGroupProfessorLink(SaveGroupProfessorLinkRequest request) {
        String insertQuery = "INSERT INTO GroupProfessorLink (prof_id, course_id, group_id) VALUES (?, ?, ?)";
        String updateQuery = "UPDATE GroupProfessorLink SET prof_id = ? WHERE course_id = ? AND group_id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                int existingProfessorId = getExistingProfessorId(connection, request.getCourseId(), request.getGroupId());
                if (existingProfessorId == -1) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                        preparedStatement.setInt(1, request.getProfessorId());
                        preparedStatement.setInt(2, request.getCourseId());
                        preparedStatement.setInt(3, request.getGroupId());
                        preparedStatement.executeUpdate();
                    }
                } else {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                        preparedStatement.setInt(1, request.getProfessorId());
                        preparedStatement.setInt(2, request.getCourseId());
                        preparedStatement.setInt(3, request.getGroupId());
                        preparedStatement.executeUpdate();
                    }
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static int getExistingProfessorId(Connection connection, int courseId, int groupId) throws SQLException {
        String query = "SELECT prof_id FROM GroupProfessorLink WHERE course_id = ? AND group_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, courseId);
            preparedStatement.setInt(2, groupId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("prof_id");
                }
            }
        }
        return -1;
    }
    public static JSONArray getSchedule() {
        JSONArray scheduleArray = new JSONArray();
        String query = "SELECT s.week_day, s.time_day, c.classroom_name, g.group_name, p.first_name, p.last_name " +
                "FROM Schedule s " +
                "JOIN GroupProfessorLink gpl ON s.link_id = gpl.id " +
                "JOIN Groups g ON gpl.group_id = g.id " +
                "JOIN Professors p ON gpl.prof_id = p.id " +
                "JOIN Classrooms c ON s.classroom_id = c.id";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                JSONObject scheduleEntry = new JSONObject();
                scheduleEntry.put("week_day", rs.getString("week_day"));
                scheduleEntry.put("time_day", rs.getString("time_day"));
                scheduleEntry.put("classroom", rs.getString("classroom_name"));
                scheduleEntry.put("group", rs.getString("group_name"));
                scheduleEntry.put("professor", rs.getString("first_name") + " " + rs.getString("last_name"));
                scheduleArray.put(scheduleEntry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Exception: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception: " + e.getMessage());
        }
        return scheduleArray;
    }



}