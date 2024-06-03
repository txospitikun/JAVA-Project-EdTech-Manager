package org.example;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.List;

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
        gbc.fill = GridBagConstraints.HORIZONTAL;
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

        JButton addButton = new JButton("Creează Profesor");
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

        String[] columnNames = {"ProfesorID", "UserID", "Prenume", "Nume", "Rang", "Nume utilizator", "Materii"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 2;
            }
        };
        JTable table = new JTable(tableModel);
        table.removeColumn(table.getColumnModel().getColumn(0));
        table.removeColumn(table.getColumnModel().getColumn(0));

        JScrollPane tableScrollPane = new JScrollPane(table);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        loadCourses();

        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                switch (column) {
                    case 2:
                        updateProfessorFirstName(row);
                        break;
                    case 3:
                        updateProfessorLastName(row);
                        break;
                    case 4:
                        updateProfessorRank(row);
                        break;
                    case 5:
                        updateProfessorUsername(row);
                        break;
                    case 6:
                        updateProfessorCourses(row);
                        break;
                }
            }
        });
    }

    private void createOrEditProfessor(String firstName, String lastName, String rank, String username, String password, List<String> courses) {
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
            jsonInput.put("courses", new JSONArray(courses));

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

                tableModel.setRowCount(0);
                for (int i = 0; i < professorsArray.length(); i++) {
                    JSONObject professor = professorsArray.getJSONObject(i);
                    int professorID = professor.getInt("id");
                    int userID = professor.getInt("user_id");
                    String firstName = professor.getString("first_name");
                    String lastName = professor.getString("last_name");
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

                    tableModel.addRow(new Object[]{professorID, userID, firstName, lastName, rank, username, coursesBuilder.toString()});
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

    private void updateProfessorFirstName(int row) {
        try {
            String url = "http://localhost:8080/api/update-professor/first_name";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("professorID", tableModel.getValueAt(row, 0));
            jsonInput.put("firstName", tableModel.getValueAt(row, 2).toString());

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Prenume actualizat cu succes!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea prenumelui!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void updateProfessorLastName(int row) {
        try {
            String url = "http://localhost:8080/api/update-professor/last_name";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("professorID", tableModel.getValueAt(row, 0));
            jsonInput.put("lastName", tableModel.getValueAt(row, 3).toString());

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Nume actualizat cu succes!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea numelui!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void updateProfessorRank(int row) {
        try {
            String url = "http://localhost:8080/api/update-professor/rank";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("professorID", tableModel.getValueAt(row, 0));
            jsonInput.put("rank", tableModel.getValueAt(row, 4).toString());

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Rang actualizat cu succes!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea rangului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void updateProfessorUsername(int row) {
        try {
            String url = "http://localhost:8080/api/update-professor/username";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("userID", tableModel.getValueAt(row, 1));
            jsonInput.put("username", tableModel.getValueAt(row, 5).toString());

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Username actualizat cu succes!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea username-ului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void updateProfessorCourses(int row) {
        try {
            String url = "http://localhost:8080/api/update-professor/courses";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("professorID", tableModel.getValueAt(row, 0));
            String coursesString = tableModel.getValueAt(row, 6).toString();
            String[] courses = coursesString.split(",\\s*");
            jsonInput.put("courses", new JSONArray(courses));

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Materii actualizate cu succes!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea materiilor!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }
}
