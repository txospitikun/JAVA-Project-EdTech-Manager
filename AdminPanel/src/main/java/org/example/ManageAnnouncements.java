package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageAnnouncements {
    public ManageAnnouncements() {
        JFrame frame = new JFrame("Admin Panel - Gestionează Anunțuri");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Gestionează Anunțuri");
        titleLabel.setBounds(10, 20, 300, 25);
        panel.add(titleLabel);

        JLabel announcementTitleLabel = new JLabel("Titlu:");
        announcementTitleLabel.setBounds(10, 50, 80, 25);
        panel.add(announcementTitleLabel);

        JTextField announcementTitleText = new JTextField(20);
        announcementTitleText.setBounds(150, 50, 165, 25);
        panel.add(announcementTitleText);

        JLabel announcementTextLabel = new JLabel("Text:");
        announcementTextLabel.setBounds(10, 80, 80, 25);
        panel.add(announcementTextLabel);

        JTextArea announcementTextArea = new JTextArea(5, 20);
        JScrollPane announcementTextScrollPane = new JScrollPane(announcementTextArea);
        announcementTextScrollPane.setBounds(150, 80, 165, 75);
        panel.add(announcementTextScrollPane);

        JButton addButton = new JButton("Trimite Anunț");
        addButton.setBounds(10, 170, 200, 25);
        panel.add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru trimiterea unui anunț
            }
        });

        JLabel listTitleLabel = new JLabel("Anunțuri Existente");
        listTitleLabel.setBounds(10, 200, 300, 25);
        panel.add(listTitleLabel);

        JPanel announcementsPanel = new JPanel();
        announcementsPanel.setLayout(new BoxLayout(announcementsPanel, BoxLayout.Y_AXIS));
        JScrollPane announcementsScrollPane = new JScrollPane(announcementsPanel);
        announcementsScrollPane.setBounds(10, 230, 760, 200);
        panel.add(announcementsScrollPane);

        // Exemplu de anunțuri existente
        addAnnouncement(announcementsPanel, "Titlul Anunțului 1", "12 Mai 2024", "Aici este textul anunțului 1. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam venenatis, velit eget fermentum pulvinar.");
        addAnnouncement(announcementsPanel, "Titlul Anunțului 2", "10 Mai 2024", "Aici este textul anunțului 2. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam venenatis, velit eget fermentum pulvinar.");
    }

    private void addAnnouncement(JPanel panel, String title, String date, String text) {
        JPanel announcementPanel = new JPanel();
        announcementPanel.setLayout(new BoxLayout(announcementPanel, BoxLayout.Y_AXIS));
        announcementPanel.setBorder(BorderFactory.createTitledBorder(title + " - " + date));

        JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane textScrollPane = new JScrollPane(textArea);
        textScrollPane.setPreferredSize(new java.awt.Dimension(700, 100));

        JButton deleteButton = new JButton("Șterge");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.remove(announcementPanel);
                panel.revalidate();
                panel.repaint();
            }
        });

        announcementPanel.add(textScrollPane);
        announcementPanel.add(deleteButton);
        panel.add(announcementPanel);
    }
}
