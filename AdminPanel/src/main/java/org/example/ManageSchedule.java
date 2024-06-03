package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class ManageSchedule extends JFrame {
    private String jwt;
    private DefaultTableModel tableModel;

    public ManageSchedule(String jwt) {
        this.jwt = jwt;

        setTitle("Admin Panel - Gestionează Orar");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);

        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Gestionează Orar");
        titleLabel.setBounds(10, 20, 300, 25);
        panel.add(titleLabel);

        JButton generateSem1Button = new JButton("Generează Orar Semestrul 1");
        generateSem1Button.setBounds(10, 50, 300, 25);
        panel.add(generateSem1Button);
        generateSem1Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateSchedule(1);
            }
        });

        JButton generateSem2Button = new JButton("Generează Orar Semestrul 2");
        generateSem2Button.setBounds(10, 80, 300, 25);
        panel.add(generateSem2Button);
        generateSem2Button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateSchedule(2);
            }
        });

        JButton loadScheduleButton = new JButton("Încarcă Orarul");
        loadScheduleButton.setBounds(10, 110, 300, 25);
        panel.add(loadScheduleButton);
        loadScheduleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSchedule();
            }
        });

        JLabel listTitleLabel = new JLabel("Orarul General");
        listTitleLabel.setBounds(10, 140, 300, 25);
        panel.add(listTitleLabel);

        tableModel = new DefaultTableModel(new String[]{"Ziua", "Interval Orar", "Clasa", "Grupa", "Nume Profesor"}, 0);
        JTable scheduleTable = new JTable(tableModel);
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
        scheduleScrollPane.setBounds(10, 170, 760, 200);
        panel.add(scheduleScrollPane);
    }

    private void generateSchedule(int semester) {
        try {
            URL url = new URL("http://localhost:8080/api/generate_schedule");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; utf-8");
            connection.setRequestProperty("Authorization", jwt);
            connection.setDoOutput(true);

            String jsonInputString = "{\"semester\":" + semester + "}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                JOptionPane.showMessageDialog(null, "Orarul a fost generat cu succes pentru semestrul " + semester + "!");
            } else {
                JOptionPane.showMessageDialog(null, "A apărut o eroare la generarea orarului.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "A apărut o eroare la generarea orarului.");
        }
    }

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

}
