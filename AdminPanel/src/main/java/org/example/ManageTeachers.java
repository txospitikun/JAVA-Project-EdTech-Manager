package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class ManageTeachers extends JFrame {
    private String jwt;

    public ManageTeachers(String jwt) {
        this.jwt = jwt;

        setTitle("Admin Panel - Gestionează Profesori");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel firstNameLabel = new JLabel("Prenume:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(firstNameLabel, gbc);

        JTextField firstNameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        JLabel lastNameLabel = new JLabel("Nume:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lastNameLabel, gbc);

        JTextField lastNameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        JLabel rankLabel = new JLabel("Rang:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(rankLabel, gbc);

        JTextField rankField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(rankField, gbc);

        JLabel usernameLabel = new JLabel("Nume utilizator:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Parolă:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JButton addButton = new JButton("Creează/Editează Profesor");
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(addButton, gbc);

        JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String rank = rankField.getText();
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                try {
                    String url = "http://localhost:8080/api/create_professor";
                    URL obj = new URL(url);
                    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                    con.setRequestMethod("POST");
                    con.setRequestProperty("Content-Type", "application/json");

                    JSONObject jsonInput = new JSONObject();
                    jsonInput.put("jwt", jwt);
                    jsonInput.put("firstname", firstName);
                    jsonInput.put("lastname", lastName);
                    jsonInput.put("rank", rank);
                    jsonInput.put("username", username);
                    jsonInput.put("password", password);

                    con.setDoOutput(true);
                    try (OutputStream os = con.getOutputStream()) {
                        byte[] input = jsonInput.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    int responseCode = con.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        statusLabel.setForeground(Color.GREEN);
                        statusLabel.setText("Profesor adăugat cu succes!");
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("JWT invalid!");
                    } else {
                        statusLabel.setForeground(Color.RED);
                        statusLabel.setText("Eroare la adăugarea profesorului!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    statusLabel.setText("Eroare de conexiune!");
                }
            }
        });

        panel.add(formPanel, BorderLayout.CENTER);
    }
}
