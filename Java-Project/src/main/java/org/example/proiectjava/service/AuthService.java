package org.example.proiectjava.service;

import org.example.proiectjava.database.DatabaseConfig;
import org.example.proiectjava.database.DatabaseConnection;
import org.example.proiectjava.dto.LoginRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {
    public static boolean authentificateUser(LoginRequest loginRequest) {
        String query = "SELECT * FROM users WHERE Username = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, loginRequest.getUsername());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        if(resultSet.getString("Password").equals(loginRequest.getPassword()))
                        {
                            return true;
                        }
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occured: " + e.getMessage());
        }
        return false;
    }
}
