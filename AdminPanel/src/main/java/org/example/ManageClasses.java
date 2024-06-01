package org.example;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

public class ManageClasses extends JFrame {
    private String jwt;
    private DefaultComboBoxModel<String> groupComboBoxModel;
    private JPanel coursesPanel;
    private JComboBox<String> groupComboBox;
    private JLabel statusLabel;
    private JButton confirmButton;

    public ManageClasses(String jwt) {
        this.jwt = jwt;

        setTitle("Admin Panel - Gestionează Grupe");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel groupNameLabel = new JLabel("Nume Grupă:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(groupNameLabel, gbc);

        JTextField groupNameField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(groupNameField, gbc);

        JButton createGroupButton = new JButton("Creează Grupă");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(createGroupButton, gbc);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        JPanel groupPanel = new JPanel(new BorderLayout());
        groupComboBoxModel = new DefaultComboBoxModel<>();
        groupComboBox = new JComboBox<>(groupComboBoxModel);
        groupComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedGroup = (String) groupComboBox.getSelectedItem();
                if (selectedGroup != null) {
                    loadCoursesForGroup(selectedGroup);
                }
            }
        });
        groupPanel.add(new JLabel("Selectează Grupă:"), BorderLayout.WEST);
        groupPanel.add(groupComboBox, BorderLayout.CENTER);
        panel.add(groupPanel, BorderLayout.CENTER);

        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        JScrollPane coursesScrollPane = new JScrollPane(coursesPanel);
        coursesScrollPane.setPreferredSize(new Dimension(760, 300));
        panel.add(coursesScrollPane, BorderLayout.SOUTH);

        createGroupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createGroup(groupNameField.getText());
            }
        });

        confirmButton = new JButton("Confirmă Modificările");
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                confirmChanges();
            }
        });
        panel.add(confirmButton, BorderLayout.SOUTH);

        loadGroups();
    }

    private void createGroup(String groupName) {
        try {
            String url = "http://localhost:8080/api/create_group";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("groupName", groupName);

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Grupă creată cu succes!");
                loadGroups();
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("JWT invalid!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la crearea grupei!");
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

                groupComboBoxModel.removeAllElements();
                for (int i = 0; i < groupsArray.length(); i++) {
                    JSONObject group = groupsArray.getJSONObject(i);
                    String groupName = group.getString("groupName");
                    groupComboBoxModel.addElement(groupName);
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

    private void loadCoursesForGroup(String groupName) {
        coursesPanel.removeAll();
        int year = Character.getNumericValue(groupName.charAt(0));
        int selectedGroupId = findGroupIdByName(groupName);
        try {
            String url = "http://localhost:8080/api/get_courses_for_year?year=" + year;
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

                JSONObject groupProfessorsResponse = getGroupProfessorsForCourses(selectedGroupId);
                JSONArray groupCourseProfessors = groupProfessorsResponse.getJSONArray("groupCourseProfessors");

                for (int i = 0; i < coursesArray.length(); i++) {
                    JSONObject course = coursesArray.getJSONObject(i);
                    int courseId = course.getInt("id");
                    String courseTitle = course.getString("courseTitle");

                    JPanel coursePanel = new JPanel();
                    coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.X_AXIS));
                    coursePanel.setBorder(BorderFactory.createTitledBorder(courseTitle));
                    coursePanel.setPreferredSize(new Dimension(750, 50)); // Dimensiune fixă pentru fiecare curs

                    JComboBox<String> professorComboBox = new JComboBox<>();
                    loadProfessorsForCourse(courseId, professorComboBox);

                    int selectedProfId = findSelectedProfessor(groupCourseProfessors, courseId);
                    if (selectedProfId != -1) {
                        professorComboBox.setSelectedItem(getProfessorNameById(selectedProfId));
                    }

                    coursePanel.add(new JLabel("Profesor:"));
                    coursePanel.add(professorComboBox);

                    coursesPanel.add(coursePanel);
                }
                coursesPanel.revalidate();
                coursesPanel.repaint();
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la încărcarea cursurilor!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private JSONObject getGroupProfessorsForCourses(int groupId) throws Exception {
        String url = "http://localhost:8080/api/get_group_course_professors?groupId=" + groupId;
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

            return new JSONObject(response.toString());
        } else {
            throw new Exception("Error loading group course professors");
        }
    }

    private int findSelectedProfessor(JSONArray groupCourseProfessors, int courseId) {
        for (int i = 0; i < groupCourseProfessors.length(); i++) {
            JSONObject groupCourseProfessor = groupCourseProfessors.getJSONObject(i);
            if (groupCourseProfessor.getInt("courseId") == courseId) {
                return groupCourseProfessor.getInt("profId");
            }
        }
        return -1;
    }

    private String getProfessorNameById(int profId) {
        try {
            String url = "http://localhost:8080/api/get_professor_by_id?profId=" + profId;
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

                System.out.println("Response from server: " + response.toString()); // Debug line
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("professorName");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la obținerea numelui profesorului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
        return null;
    }

    private void loadProfessorsForCourse(int courseId, JComboBox<String> professorComboBox) {
        try {
            String url = "http://localhost:8080/api/get_professors_for_course?courseId=" + courseId;
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

                professorComboBox.removeAllItems();
                for (int i = 0; i < professorsArray.length(); i++) {
                    JSONObject professor = professorsArray.getJSONObject(i);
                    String professorName = professor.getString("name");
                    professorComboBox.addItem(professorName);
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

    private void confirmChanges() {
        try {
            String selectedGroup = (String) groupComboBox.getSelectedItem();
            if (selectedGroup == null) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Nicio grupă selectată!");
                return;
            }

            int groupId = findGroupIdByName(selectedGroup);
            if (groupId == -1) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Grupă invalidă!");
                return;
            }

            JSONArray groupProfessorLinks = new JSONArray();

            for (Component comp : coursesPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel coursePanel = (JPanel) comp;
                    Border border = coursePanel.getBorder();
                    String courseTitle = border instanceof TitledBorder ? ((TitledBorder) border).getTitle() : null;
                    if (courseTitle != null) {
                        int courseId = findCourseIdByName(courseTitle);
                        if (courseId != -1) {
                            JComboBox<String> professorComboBox = (JComboBox<String>) coursePanel.getComponent(1);
                            String professorName = (String) professorComboBox.getSelectedItem();
                            int professorId = findProfessorIdByName(professorName);

                            JSONObject link = new JSONObject();
                            link.put("profId", professorId);
                            link.put("courseId", courseId);
                            link.put("groupId", groupId);
                            groupProfessorLinks.put(link);
                        }
                    }
                }
            }

            String url = "http://localhost:8080/api/update_group_professors";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("groupProfessorLinks", groupProfessorLinks);

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Modificările au fost confirmate!");
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("JWT invalid!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la confirmarea modificărilor!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private int findCourseIdByName(String courseTitle) {
        try {
            String url = "http://localhost:8080/api/find_course_id_by_name?courseTitle=" + URLEncoder.encode(courseTitle, "UTF-8");
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
                return jsonResponse.getInt("courseId");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la găsirea ID-ului cursului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
        return -1;
    }

    private int findProfessorIdByName(String professorName) {
        try {
            String url = "http://localhost:8080/api/find_professor_id_by_name?professorName=" + URLEncoder.encode(professorName, "UTF-8");
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
                return jsonResponse.getInt("professorId");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la găsirea ID-ului profesorului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
        return -1;
    }

}
