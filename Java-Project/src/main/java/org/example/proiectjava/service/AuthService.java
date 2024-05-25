package org.example.proiectjava.service;

import org.example.proiectjava.database.DatabaseConfig;
import org.example.proiectjava.database.DatabaseConnection;
import org.example.proiectjava.dto.LoginRequest;
import org.example.proiectjava.dto.RegisterRequest;
import org.json.JSONObject;
import org.json.JSONString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthService {
    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    public static String authentificateUser(LoginRequest loginRequest) {
        String query = "SELECT * FROM users WHERE Username = ?";
        try (Connection connection = DatabaseConfig.getConnection()) {
            if (connection != null) {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, loginRequest.getUsername());
                logger.info(EncryptionService.encryptSHA256(loginRequest.getPassword()));
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

    public static int registerUser(RegisterRequest registerRequest)
    {
        String searchQuery = "SELECT * FROM Users WHERE Username = ?";
        String query = "INSERT INTO Users (Username, Password, Privilege) VALUES (?, ?, ?)";
        try(Connection connection = DatabaseConfig.getConnection())
        {
            if(connection != null)
            {
                PreparedStatement searchStatement = connection.prepareStatement(searchQuery);
                searchStatement.setString(1, registerRequest.getUsername());

                try(ResultSet result = searchStatement.executeQuery())
                {
                    if(result.next())
                    {
                        return 2;
                    }
                }


                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, registerRequest.getUsername());
                preparedStatement.setString(2, EncryptionService.encryptSHA256(registerRequest.getPassword()));
                preparedStatement.setInt(3, registerRequest.getPrivilege());

                int affectedRows = preparedStatement.executeUpdate();
                return 1;
                //handle register
            }
        } catch (Exception e) {
            System.err.println("An unexpected SQL exception has occured: " + e.getMessage());
        }
        return 0;
    }
}
