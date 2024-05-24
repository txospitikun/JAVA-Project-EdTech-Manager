package org.example.proiectjava.service;

import org.example.proiectjava.database.DatabaseConfig;
import org.example.proiectjava.database.DatabaseConnection;
import org.example.proiectjava.dto.LoginRequest;
import org.example.proiectjava.dto.RegisterRequest;
import org.json.JSONObject;
import org.json.JSONString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {
    public static String authentificateUser(LoginRequest loginRequest) {
        String query = "SELECT * FROM users WHERE Username = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, loginRequest.getUsername());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        if(resultSet.getString("Password").equals(EncryptionService.encryptSHA256(loginRequest.getPassword())))
                        {
                            JSONObject userInfo = new JSONObject();
                            userInfo.put("Username", resultSet.getString("Username"));
                            userInfo.put("Privelege", resultSet.getString("Privilege"));
                            return EncryptionService.generateToken(userInfo.toString());
                        }
                        return "";
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occured: " + e.getMessage());
        }
        return "";
    }

    public static void registerUser(RegisterRequest registerRequest)
    {
        String query = "INSERT INTO Users (Username, Password, Privilege) VALUES (?, ?, ?)";
        try(Connection connection = DatabaseConfig.getConnection())
        {
            if(connection != null)
            {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, registerRequest.getUsername());
                preparedStatement.setString(2, EncryptionService.encryptSHA256(registerRequest.getPassword()));
                preparedStatement.setString(3, String.valueOf(registerRequest.getPrivilege()));

                try(ResultSet resultSet = preparedStatement.executeQuery())
                {
                    System.out.println(resultSet);
                }
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occured: " + e.getMessage());
        }
    }
}
