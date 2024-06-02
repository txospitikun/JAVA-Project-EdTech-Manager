package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

public class ManageClasses extends JFrame {
    private String jwt;
    private DefaultListModel<String> groupListModel;
    private JPanel coursesPanel;
    private JList<String> groupList;
    private JLabel statusLabel;
    private Map<Integer, JComboBox<ProfessorItem>> courseProfessorMap = new HashMap<>();
    private Map<Integer, Integer> courseIdMap = new HashMap<>(); // map to store courseId with index

    public ManageClasses(String jwt) {
        this.jwt = jwt;

        setTitle("Admin Panel - Gestionează Grupe");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);

        JPanel leftPanel = new JPanel(new BorderLayout());
        mainPanel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        mainPanel.add(rightPanel, BorderLayout.CENTER);

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

        leftPanel.add(formPanel, BorderLayout.NORTH);

        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);
        groupList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        groupList.addListSelectionListener(e -> {
            String selectedGroup = groupList.getSelectedValue();
            if (selectedGroup != null) {
                loadCoursesForGroup(selectedGroup);
            }
        });

        JScrollPane groupScrollPane = new JScrollPane(groupList);
        leftPanel.add(groupScrollPane, BorderLayout.CENTER);

        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        JScrollPane coursesScrollPane = new JScrollPane(coursesPanel);
        rightPanel.add(coursesScrollPane, BorderLayout.CENTER);

        createGroupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createGroup(groupNameField.getText());
            }
        });

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

    private void loadCoursesForGroup(String groupName) {
        coursesPanel.removeAll();
        int year = Character.getNumericValue(groupName.charAt(0));
        int selectedGroupId = findGroupIdByName(groupName);
        try {
            String url = "http://localhost:8080/api/get_courses_for_year?year=" + URLEncoder.encode(String.valueOf(year), "UTF-8");
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
                    coursePanel.setLayout(new BoxLayout(coursePanel, BoxLayout.Y_AXIS));
                    coursePanel.setBorder(BorderFactory.createTitledBorder(courseTitle));

                    JComboBox<ProfessorItem> professorComboBox = new JComboBox<>();
                    loadProfessorsForCourse(courseId, professorComboBox);
                    courseProfessorMap.put(courseId, professorComboBox);

                    int selectedProfId = findSelectedProfessor(groupCourseProfessors, courseId);
                    if (selectedProfId != -1) {
                        professorComboBox.setSelectedItem(new ProfessorItem(selectedProfId, getProfessorNameById(selectedProfId)));
                    }

                    JButton saveButton = new JButton("Salvează");
                    saveButton.addActionListener(e -> {
                        try {
                            ProfessorItem selectedProfessor = (ProfessorItem) professorComboBox.getSelectedItem();
                            if (selectedProfessor == null) {
                                statusLabel.setForeground(Color.RED);
                                statusLabel.setText("Trebuie sa selectezi un profesor");
                            } else {
                                saveProfessorForCourse(selectedGroupId, courseId, selectedProfessor.getId());
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            statusLabel.setText("Eroare de conexiune!");
                        }
                    });

                    coursePanel.add(professorComboBox);
                    coursePanel.add(saveButton);

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

    private void saveProfessorForCourse(int groupId, int courseId, int professorId) {
        try {
            String url = "http://localhost:8080/api/save_group_professor_link";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonRequest = new JSONObject();
            jsonRequest.put("jwt", jwt);
            jsonRequest.put("groupId", groupId);
            jsonRequest.put("courseId", courseId);
            jsonRequest.put("professorId", professorId);

            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonRequest.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Profesor salvat cu succes!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la salvarea profesorului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private JSONObject getGroupProfessorsForCourses(int groupId) throws Exception {
        String url = "http://localhost:8080/api/get_group_course_professors?groupId=" + URLEncoder.encode(String.valueOf(groupId), "UTF-8");
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
            String url = "http://localhost:8080/api/get_professor_by_id?profId=" + URLEncoder.encode(String.valueOf(profId), "UTF-8");
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

    private void loadProfessorsForCourse(int courseId, JComboBox<ProfessorItem> professorComboBox) {
        try {
            String url = "http://localhost:8080/api/get_professors_for_course?courseId=" + URLEncoder.encode(String.valueOf(courseId), "UTF-8");
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
                    int professorId = professor.getInt("id");
                    String professorName = professor.getString("name");
                    professorComboBox.addItem(new ProfessorItem(professorId, professorName));
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
            String url = "http://localhost:8080/api/find_group_id_by_name?name=" + URLEncoder.encode(groupName, "UTF-8");
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


    private static class ProfessorItem {
        private final int id;
        private final String name;

        public ProfessorItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
