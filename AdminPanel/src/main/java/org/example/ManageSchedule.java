package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageSchedule {
    public ManageSchedule() {
        JFrame frame = new JFrame("Admin Panel - Gestionează Orar");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Gestionează Orar");
        titleLabel.setBounds(10, 20, 300, 25);
        panel.add(titleLabel);

        JLabel dayLabel = new JLabel("Ziua:");
        dayLabel.setBounds(10, 50, 80, 25);
        panel.add(dayLabel);

        JComboBox<String> dayCombo = new JComboBox<>(new String[]{"Luni", "Marți", "Miercuri", "Joi", "Vineri", "Sâmbătă", "Duminică"});
        dayCombo.setBounds(150, 50, 165, 25);
        panel.add(dayCombo);

        JLabel timeLabel = new JLabel("Ora:");
        timeLabel.setBounds(10, 80, 80, 25);
        panel.add(timeLabel);

        JTextField timeText = new JTextField(20);
        timeText.setBounds(150, 80, 165, 25);
        panel.add(timeText);

        JLabel classLabel = new JLabel("Grupă:");
        classLabel.setBounds(10, 110, 80, 25);
        panel.add(classLabel);

        JTextField classText = new JTextField(20);
        classText.setBounds(150, 110, 165, 25);
        panel.add(classText);

        JLabel subjectLabel = new JLabel("Materie:");
        subjectLabel.setBounds(10, 140, 80, 25);
        panel.add(subjectLabel);

        JTextField subjectText = new JTextField(20);
        subjectText.setBounds(150, 140, 165, 25);
        panel.add(subjectText);

        JLabel teacherLabel = new JLabel("Profesor:");
        teacherLabel.setBounds(10, 170, 80, 25);
        panel.add(teacherLabel);

        JTextField teacherText = new JTextField(20);
        teacherText.setBounds(150, 170, 165, 25);
        panel.add(teacherText);

        JLabel roomLabel = new JLabel("Sala:");
        roomLabel.setBounds(10, 200, 80, 25);
        panel.add(roomLabel);

        JTextField roomText = new JTextField(20);
        roomText.setBounds(150, 200, 165, 25);
        panel.add(roomText);

        JButton addButton = new JButton("Adaugă Ora");
        addButton.setBounds(10, 230, 200, 25);
        panel.add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru adăugarea unei ore
            }
        });

        JButton generateButton = new JButton("Generează Orar");
        generateButton.setBounds(220, 230, 200, 25);
        panel.add(generateButton);
        generateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru generarea automată a orarului
                JOptionPane.showMessageDialog(null, "Orarul a fost generat!");
            }
        });

        JLabel listTitleLabel = new JLabel("Orarul General");
        listTitleLabel.setBounds(10, 260, 300, 25);
        panel.add(listTitleLabel);

        JTable scheduleTable = new JTable(new String[][]{
                {"Luni", "08:00", "10A", "Matematică", "Prof. Popescu", "Sala 101"}
        }, new String[]{"Ziua", "Ora", "Grupă", "Materie", "Profesor", "Sala"});
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
        scheduleScrollPane.setBounds(10, 290, 760, 200);
        panel.add(scheduleScrollPane);

        JButton editButton = new JButton("Edit");
        editButton.setBounds(10, 500, 80, 25);
        panel.add(editButton);
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru editarea unei ore
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(100, 500, 80, 25);
        panel.add(deleteButton);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru ștergerea unei ore
            }
        });
    }
<<<<<<< Updated upstream
=======

    private void loadSchedule() {
        try {
            URL url = new URL("http://localhost:8080/api/get_schedule");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", jwt);

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray scheduleArray = jsonResponse.getJSONArray("schedule");

                tableModel.setRowCount(0);
                for (int i = 0; i < scheduleArray.length(); i++) {
                    JSONObject entry = scheduleArray.getJSONObject(i);
                    String weekDay = entry.getString("week_day");
                    String timeDay = entry.getString("time_day");
                    String classroom = entry.getString("classroom");
                    String group = entry.getString("group");
                    String professor = entry.getString("professor");

                    tableModel.addRow(new Object[]{weekDay, timeDay, classroom, group, professor});
                }
            } else {
                System.out.println("Error Response: " + responseCode);
                JOptionPane.showMessageDialog(null, "A apărut o eroare la încărcarea orarului.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "A apărut o eroare la încărcarea orarului.");
        }
    }

>>>>>>> Stashed changes
}
