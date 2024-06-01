package org.example.proiectjava.service;

import org.example.proiectjava.database.DatabaseConfig;
import org.example.proiectjava.dto.CreateProfessorRequest;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.util.List;

public class RecordService {
    public static int registerProfessor(CreateProfessorRequest createProfessorRequest) {
        int registeredUserID = -1;
        if ((registeredUserID = AuthService.registerUser(createProfessorRequest.getUsername(), createProfessorRequest.getPassword(), 2)) == -1) {
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
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT p.id, p.first_name, p.last_name, p.rank, u.username FROM professors p JOIN users u ON p.user_id = u.id")) {

            while (rs.next()) {
                JSONObject professor = new JSONObject();
                professor.put("id", rs.getInt("id"));
                professor.put("firstName", rs.getString("first_name"));
                professor.put("lastName", rs.getString("last_name"));
                professor.put("rank", rs.getString("rank"));
                professor.put("username", rs.getString("username"));
                // Add other columns as needed

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
}
