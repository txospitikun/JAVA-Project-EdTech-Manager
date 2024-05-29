package org.example.proiectjava.service;

import org.example.proiectjava.database.DatabaseConfig;
import org.example.proiectjava.database.DatabaseConnection;
import org.example.proiectjava.dto.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import java.sql.*;
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

        int foundCourseID = -1;
        for (var course : courses) {
            String query = "SELECT ID FROM Courses WHERE course_title = ?";
            try (Connection connection = DatabaseConfig.getConnection()) {
                if (connection != null) {
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, course);

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


    public static JSONArray getAllProfessors() {
        JSONArray professorsArray = new JSONArray();
        String query = "SELECT p.*, u.* FROM Professors p JOIN Users u ON p.user_id = u.id";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                JSONObject professor = new JSONObject();
                professor.put("professorId", rs.getInt("p.ID"));
                professor.put("firstName", rs.getString("p.FIRST_NAME"));
                professor.put("lastName", rs.getString("p.LAST_NAME"));
                professor.put("rank", rs.getString("p.RANK"));
                professor.put("userId", rs.getInt("p.USER_ID"));
                professor.put("username", rs.getString("u.USERNAME"));
                // Add any additional columns from the Users table if needed
                professorsArray.put(professor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return professorsArray;
    }


    public static boolean updateProfessor(EditProfessorRequest editProfessorRequest) {
        String updateProfessorQuery = "UPDATE Professors SET first_name = ?, last_name = ?, rank = ? WHERE ID = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(updateProfessorQuery);
                preparedStatement.setString(1, editProfessorRequest.getFirstName());
                preparedStatement.setString(2, editProfessorRequest.getLastName());
                preparedStatement.setString(3, editProfessorRequest.getRank());
                preparedStatement.setInt(4, editProfessorRequest.getProfessorID());

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    updateProfessorCourses(editProfessorRequest.getProfessorID(), editProfessorRequest.getCourses());
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occurred: " + e.getMessage());
        }
        return false;
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

    public static boolean updateCourse(EditCourseRequest editCourseRequest) {
        String query = "UPDATE Courses SET course_title = ?, year = ?, semester = ?, credits = ? WHERE id = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, editCourseRequest.getCourseTitle());
                preparedStatement.setInt(2, editCourseRequest.getYear());
                preparedStatement.setInt(3, editCourseRequest.getSemester());
                preparedStatement.setInt(4, editCourseRequest.getCredits());
                preparedStatement.setInt(5, editCourseRequest.getCourseId());

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
        if ((registeredUserID = AuthService.registerUser(createStudentRequest.getUsername(), createStudentRequest.getPassword(), createStudentRequest.getPrivilege())) == -1) {
            return -1;
        }

        String query = "INSERT INTO Students (nr_matricol, first_name, last_name, user_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, createStudentRequest.getNrMatricol());
                preparedStatement.setString(2, createStudentRequest.getFirstName());
                preparedStatement.setString(3, createStudentRequest.getLastName());
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

    public static int registerStudentYear(CreateStudentYearRequest createStudentYearRequest) {
        String query = "INSERT INTO StudentYears (id_student, year, study_year, group_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, createStudentYearRequest.getStudentId());
                preparedStatement.setInt(2, createStudentYearRequest.getYear());
                preparedStatement.setInt(3, createStudentYearRequest.getStudyYear());
                preparedStatement.setInt(4, createStudentYearRequest.getGroupId());

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
}
