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
import org.json.JSONArray;
import org.json.JSONObject;

public class ManageAnnouncements extends JFrame {
    private String jwt;
    private JPanel announcementsPanel;
    private JScrollPane announcementsScrollPane;
    private JLabel statusLabel;

    public ManageAnnouncements(String jwt) {
        this.jwt = jwt;

        setTitle("Admin Panel - Gestionează Anunțuri");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Titlu:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(titleLabel, gbc);

        JTextField titleField = new JTextField(20);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(titleField, gbc);

        JLabel textLabel = new JLabel("Text:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(textLabel, gbc);

        JTextArea textArea = new JTextArea(5, 20);
        JScrollPane textScrollPane = new JScrollPane(textArea);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(textScrollPane, gbc);

        JButton addButton = new JButton("Trimite Anunț");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(addButton, gbc);

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formPanel.add(statusLabel, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        JButton loadAnnouncementsButton = new JButton("Încarcă Anunțuri Existente");
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(loadAnnouncementsButton, gbc);

        announcementsPanel = new JPanel();
        announcementsPanel.setLayout(new BoxLayout(announcementsPanel, BoxLayout.Y_AXIS));
        announcementsScrollPane = new JScrollPane(announcementsPanel);
        announcementsScrollPane.setPreferredSize(new Dimension(760, 300));
        panel.add(announcementsScrollPane, BorderLayout.CENTER);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createAnnouncement(titleField.getText(), textArea.getText());
            }
        });

        loadAnnouncementsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadAnnouncements();
            }
        });

        setVisible(true);
    }

    private void createAnnouncement(String title, String content) {
        try {
            String url = "http://localhost:8080/api/create_announcement";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("announcementTitle", title);
            jsonInput.put("announcementContent", content);

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Anunț creat cu succes!");
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("JWT invalid!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la crearea anunțului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void loadAnnouncements() {
        try {
            String url = "http://localhost:8080/api/get_announcements";
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
                JSONArray announcementsArray = jsonResponse.getJSONArray("announcements");

                announcementsPanel.removeAll();
                for (int i = 0; i < announcementsArray.length(); i++) {
                    JSONObject announcement = announcementsArray.getJSONObject(i);
                    String title = announcement.getString("announcementTitle");
                    String date = announcement.getString("uploadDate");
                    String content = announcement.getString("announcementContent");
                    int id = announcement.getInt("id");

                    addAnnouncement(announcementsPanel, title, date, content, id);
                }
                announcementsPanel.revalidate();
                announcementsPanel.repaint();
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la încărcarea anunțurilor!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void addAnnouncement(JPanel panel, String title, String date, String text, int id) {
        JPanel announcementPanel = new JPanel();
        announcementPanel.setLayout(new BoxLayout(announcementPanel, BoxLayout.Y_AXIS));
        announcementPanel.setBorder(BorderFactory.createTitledBorder(title + " - " + date));

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textScrollPane = new JScrollPane(textArea);
        textScrollPane.setPreferredSize(new Dimension(700, 100));

        JButton editButton = new JButton("Editare");
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showEditDialog(id, title, text);
            }
        });

        JButton deleteButton = new JButton("Șterge");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAnnouncement(id);
                panel.remove(announcementPanel);
                panel.revalidate();
                panel.repaint();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        announcementPanel.add(textScrollPane);
        announcementPanel.add(buttonPanel);
        panel.add(announcementPanel);
    }

    private void showEditDialog(int id, String oldTitle, String oldContent) {
        JDialog dialog = new JDialog(this, "Editare Anunț", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Titlu:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(titleLabel, gbc);

        JTextField titleField = new JTextField(oldTitle, 20);
        gbc.gridx = 1;
        dialog.add(titleField, gbc);

        JLabel contentLabel = new JLabel("Text:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(contentLabel, gbc);

        JTextArea contentArea = new JTextArea(oldContent, 5, 20);
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        dialog.add(contentScrollPane, gbc);

        JButton saveButton = new JButton("Salvează");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        dialog.add(saveButton, gbc);

        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateAnnouncement(id, titleField.getText(), contentArea.getText());
                dialog.dispose();
            }
        });

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateAnnouncement(int id, String title, String content) {
        try {
            String url = "http://localhost:8080/api/update_announcement";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("PUT");
            con.setRequestProperty("Content-Type", "application/json");

            JSONObject jsonInput = new JSONObject();
            jsonInput.put("jwt", jwt);
            jsonInput.put("id", id);
            jsonInput.put("announcementTitle", title);
            jsonInput.put("announcementContent", content);

            con.setDoOutput(true);
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInput.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Anunț actualizat cu succes!");
                loadAnnouncements();
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("JWT invalid!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la actualizarea anunțului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }

    private void deleteAnnouncement(int id) {
        try {
            String url = "http://localhost:8080/api/delete_announcement?id=" + id;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("DELETE");
            con.setRequestProperty("Authorization", jwt);

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                statusLabel.setForeground(Color.GREEN);
                statusLabel.setText("Anunț șters cu succes!");
                loadAnnouncements();
            } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("JWT invalid!");
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Eroare la ștergerea anunțului!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Eroare de conexiune!");
        }
    }


}
