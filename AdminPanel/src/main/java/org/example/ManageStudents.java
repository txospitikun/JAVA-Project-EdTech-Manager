package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
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

public class ManageStudents extends JFrame {
    private String jwt;
    private DefaultListModel<String> groupListModel;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public ManageStudents(String jwt) {
        this.jwt = jwt;

        setTitle("Admin Panel - Gestionează Elevi");
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

        JLabel usernameLabel = new JLabel("Nume utilizator:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(usernameLabel, gbc);

        JTextField usernameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Parolă:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(passwordLabel, gbc);

        JPasswordField passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, gbc);

        JLabel groupLabel = new JLabel("Grupă:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(groupLabel, gbc);

        groupListModel = new DefaultListModel<>();
        JList<String> groupList = new JList<>(groupListModel);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane groupScrollPane = new JScrollPane(groupList);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(groupScrollPane, gbc);

        JButton addButton = new JButton("Creează Elev");
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(addButton, gbc);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        JButton loadStudentsButton = new JButton("Încarcă Elevi Existenti");
        gbc.gridx = 1;
        gbc.gridy = 8;
        formPanel.add(loadStudentsButton, gbc);

        loadStudentsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadStudents();
            }
        });

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createOrEditStudent(firstNameField.getText(), lastNameField.getText(), usernameField.getText(),
                        new String(passwordField.getPassword()), groupList.getSelectedValue());
            }
        });

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID Student", "ID Utilizator", "ID Grupă", "Nr Matricol", "Prenume", "Nume", "Nume utilizator", "Grupă"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 3;
            }
        };
        JTable table = new JTable(tableModel);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMinWidth(0);
        columnModel.getColumn(0).setMaxWidth(0);
        columnModel.getColumn(0).setWidth(0);
        columnModel.getColumn(1).setMinWidth(0);
        columnModel.getColumn(1).setMaxWidth(0);
        columnModel.getColumn(1).setWidth(0);
        columnModel.getColumn(2).setMinWidth(0);
        columnModel.getColumn(2).setMaxWidth(0);
        columnModel.getColumn(2).setWidth(0);
        JScrollPane tableScrollPane = new JScrollPane(table);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        loadGroups();
        loadStudents();

        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            switch (column) {
                case 4:
                    updateStudentFirstName(row);
                    break;
                case 5:
                    updateStudentLastName(row);
                    break;
                case 6:
                    updateStudentUsername(row);
                    break;
                case 7:
                    updateStudentGroup(row);
                    break;
            }
        });
    }

    private void createOrEditStudent(String firstName, String lastName, String username, String password, String group) {
        try {
            String url = "http://localhost:8080/api/create_student";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("firstName", firstName);
            jsonInput.put("lastName", lastName);
            jsonInput.put("username", username);
            jsonInput.put("password", password);
            jsonInput.put("group", group);

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Elev adăugat cu succes!");
                loadStudents();
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("JWT invalid!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la adăugarea elevului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void loadGroups() {
        try {
            String url = "http://localhost:8080/api/get_groups";
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
                JSONArray groupsArray = jsonResponse.getJSONArray("groups");

                groupListModel.clear();
                for (int i = 0; i < groupsArray.length(); i++) {
                    JSONObject group = groupsArray.getJSONObject(i);
                    String groupName = group.getString("groupName");
                    groupListModel.addElement(groupName);
                }
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la încărcarea grupelor!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void loadStudents() {
        try {
            String url = "http://localhost:8080/api/get_current_year_students";
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
                JSONArray studentsArray = jsonResponse.getJSONArray("students");

                tableModel.setRowCount(0);
                for (int i = 0; i < studentsArray.length(); i++) {
                    JSONObject student = studentsArray.getJSONObject(i);
                    int studentId = student.getInt("studentId");
                    int userId = student.getInt("userId");
                    int groupId = student.getInt("groupId");
                    String nrMatricol = student.getString("nrMatricol");
                    String firstName = student.getString("firstName");
                    String lastName = student.getString("lastName");
                    String username = student.getString("username");
                    String group = student.getString("group");

                    tableModel.addRow(new Object[]{studentId, userId, groupId, nrMatricol, firstName, lastName, username, group});
                }
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la încărcarea elevilor!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void updateStudentFirstName(int row) {
        try {
            String url = "http://localhost:8080/api/update_student/first_name";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("studentId", tableModel.getValueAt(row, 0));
            jsonInput.put("firstName", tableModel.getValueAt(row, 4));

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

    private void updateStudentLastName(int row) {
        try {
            String url = "http://localhost:8080/api/update_student/last_name";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("studentId", tableModel.getValueAt(row, 0));
            jsonInput.put("lastName", tableModel.getValueAt(row, 5));

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

    private void updateStudentUsername(int row) {
        try {
            String url = "http://localhost:8080/api/update_student/username";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("studentId", tableModel.getValueAt(row, 0));
            jsonInput.put("username", tableModel.getValueAt(row, 6));

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

    private void updateStudentGroup(int row) {
        try {
            String url = "http://localhost:8080/api/update_student/group";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            int studentId = (int) tableModel.getValueAt(row, 0);
            String group = (String) tableModel.getValueAt(row, 7);
            if (group == null || group.isEmpty()) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Grupă invalidă!");
                return;
            }
            int groupId = findGroupIdByName(group);

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("studentId", studentId);
            jsonInput.put("group", group);

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Grupă actualizată cu succes!");
                tableModel.setValueAt(groupId, row, 2);
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea grupei!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }


    private int findGroupIdByName(String groupName) {
        try {
            String url = "http://localhost:8080/api/find_group_id_by_name?name=" + groupName;
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
                return jsonResponse.getInt("groupId");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la găsirea ID-ului grupei!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
        return -1;
    }
}
