package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class ManageTeachers extends JFrame {
    private String jwt;
    private DefaultListModel<String> coursesListModel;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

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
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(firstNameField, gbc);

        JLabel lastNameLabel = new JLabel("Nume:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lastNameLabel, gbc);

        JTextField lastNameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(lastNameField, gbc);

        JLabel rankLabel = new JLabel("Rang:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(rankLabel, gbc);

        JTextField rankField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(rankField, gbc);

        JLabel usernameLabel = new JLabel("Nume utilizator:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Parolă:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, gbc);

        JLabel coursesLabel = new JLabel("Materii:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(coursesLabel, gbc);

        coursesListModel = new DefaultListModel<>();
        JList<String> coursesList = new JList<>(coursesListModel);
        coursesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane coursesScrollPane = new JScrollPane(coursesList);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(coursesScrollPane, gbc);

        JButton addButton = new JButton("Creează/Editează Profesor");
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(addButton, gbc);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        JButton loadProfessorsButton = new JButton("Încarcă Profesori Existenti");
        gbc.gridx = 1;
        gbc.gridy = 9;
        formPanel.add(loadProfessorsButton, gbc);

        loadProfessorsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadProfessors();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createOrEditProfessor(firstNameField.getText(), lastNameField.getText(), rankField.getText(),
                        usernameField.getText(), new String(passwordField.getPassword()), coursesList.getSelectedValuesList());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"Prenume", "Nume", "Rang", "Nume utilizator", "Materii"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(table);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        loadCourses();
    }

    private void createOrEditProfessor(String firstName, String lastName, String rank, String username, String password, java.util.List<String> courses) {
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
            jsonInput.put("courses", new JSONArray(courses)); // Trimitere doar a numelor cursurilor

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Profesor adăugat cu succes!");
                loadProfessors();
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




    private void loadProfessors() {
        try {
            String url = "http://localhost:8080/api/get_professors";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", jwt);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray professorsArray = jsonResponse.getJSONArray("professors");

                tableModel.setRowCount(0); // Clear existing rows
                for (int i = 0; i < professorsArray.length(); i++) {
                    JSONObject professor = professorsArray.getJSONObject(i);
                    String firstName = professor.getString("firstName");
                    String lastName = professor.getString("lastName");
                    String rank = professor.getString("rank");
                    String username = professor.getString("username");
                    JSONArray coursesArray = professor.optJSONArray("courses");

                    StringBuilder coursesBuilder = new StringBuilder();
                    if (coursesArray != null) {
                        for (int j = 0; j < coursesArray.length(); j++) {
                            String course = coursesArray.optString(j, null);
                            if (course != null) {
                                if (j > 0) {
                                    coursesBuilder.append(", ");
                                }
                                coursesBuilder.append(course);
                            }
                        }
                    }

                    tableModel.addRow(new Object[]{firstName, lastName, rank, username, coursesBuilder.toString()});
                }
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la încărcarea profesorilor!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }


    private void loadCourses() {
        try {
            String url = "http://localhost:8080/api/get_courses";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", jwt);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray coursesArray = jsonResponse.getJSONArray("courses");

                coursesListModel.clear();
                for (int i = 0; i < coursesArray.length(); i++) {
                    JSONObject course = coursesArray.getJSONObject(i);
                    String courseTitle = course.getString("courseTitle");
                    coursesListModel.addElement(courseTitle);
                }
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la încărcarea cursurilor!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }


}
