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

public class ManageCourses extends JFrame {
    private String jwt;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public ManageCourses(String jwt) {
        this.jwt = jwt;

        setTitle("Admin Panel - Gestionează Cursuri");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Titlu Curs:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);

        JTextField titleField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(titleField, gbc);

        JLabel yearLabel = new JLabel("An:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(yearLabel, gbc);

        JTextField yearField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(yearField, gbc);

        JLabel semesterLabel = new JLabel("Semestru:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(semesterLabel, gbc);

        JTextField semesterField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(semesterField, gbc);

        JLabel creditsLabel = new JLabel("Credite:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(creditsLabel, gbc);

        JTextField creditsField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(creditsField, gbc);

        JButton addButton = new JButton("Adaugă Curs");
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(addButton, gbc);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        JButton loadCoursesButton = new JButton("Încarcă Cursuri Existente");
        gbc.gridx = 1;
        gbc.gridy = 6;
        formPanel.add(loadCoursesButton, gbc);

        String[] columnNames = {"ID Curs", "Titlu Curs", "An", "Semestru", "Credite"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column > 0; // permite editarea doar a coloanelor relevante
            }
        };
        JTable table = new JTable(tableModel);
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMinWidth(0);
        columnModel.getColumn(0).setMaxWidth(0);
        columnModel.getColumn(0).setWidth(0);
        JScrollPane tableScrollPane = new JScrollPane(table);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createCourse(titleField.getText(), Integer.parseInt(yearField.getText()), Integer.parseInt(semesterField.getText()), Integer.parseInt(creditsField.getText()));
            }
        });

        loadCoursesButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadCourses();
            }
        });

        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            switch (column) {
                case 1:
                    updateCourseTitle(row);
                    break;
                case 2:
                    updateCourseYear(row);
                    break;
                case 3:
                    updateCourseSemester(row);
                    break;
                case 4:
                    updateCourseCredits(row);
                    break;
            }
        });

        setVisible(true);
    }

    private void createCourse(String title, int year, int semester, int credits) {
        try {
            String url = "http://localhost:8080/api/create_course";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("courseTitle", title);
            jsonInput.put("year", year);
            jsonInput.put("semester", semester);
            jsonInput.put("credits", credits);

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Curs adăugat cu succes!");
                loadCourses();
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("JWT invalid!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la adăugarea cursului!");
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

                tableModel.setRowCount(0); // Clear existing rows
                for (int i = 0; i < coursesArray.length(); i++) {
                    JSONObject course = coursesArray.getJSONObject(i);
                    int courseId = course.getInt("id");
                    String courseTitle = course.getString("courseTitle");
                    int year = course.getInt("year");
                    int semester = course.getInt("semester");
                    int credits = course.getInt("credits");

                    tableModel.addRow(new Object[]{courseId, courseTitle, year, semester, credits});
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

    private void updateCourseTitle(int row) {
        try {
            String url = "http://localhost:8080/api/update_course/title";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("courseId", tableModel.getValueAt(row, 0));
            jsonInput.put("courseTitle", tableModel.getValueAt(row, 1));

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Titlu curs actualizat cu succes!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea titlului cursului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void updateCourseYear(int row) {
        try {
            String url = "http://localhost:8080/api/update_course/year";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("courseId", tableModel.getValueAt(row, 0));
            jsonInput.put("year", tableModel.getValueAt(row, 2));

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("An curs actualizat cu succes!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea anului cursului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void updateCourseSemester(int row) {
        try {
            String url = "http://localhost:8080/api/update_course/semester";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("courseId", tableModel.getValueAt(row, 0));
            jsonInput.put("semester", tableModel.getValueAt(row, 3));

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Semestru curs actualizat cu succes!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea semestrului cursului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void updateCourseCredits(int row) {
        try {
            String url = "http://localhost:8080/api/update_course/credits";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("courseId", tableModel.getValueAt(row, 0));
            jsonInput.put("credits", tableModel.getValueAt(row, 4));

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Credite curs actualizate cu succes!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea creditelor cursului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }


}
