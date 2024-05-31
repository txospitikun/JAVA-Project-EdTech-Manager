package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageStudents {
    public ManageStudents() {
        JFrame frame = new JFrame("Admin Panel - Gestionează Elevi");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Gestionează Elevi");
        titleLabel.setBounds(10, 20, 300, 25);
        panel.add(titleLabel);

        JLabel nameLabel = new JLabel("Nume:");
        nameLabel.setBounds(10, 50, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(150, 50, 165, 25);
        panel.add(nameText);

        JLabel groupLabel = new JLabel("Grupă:");
        groupLabel.setBounds(10, 80, 80, 25);
        panel.add(groupLabel);

        JTextField groupText = new JTextField(20);
        groupText.setBounds(150, 80, 165, 25);
        panel.add(groupText);

        JLabel usernameLabel = new JLabel("Nume utilizator:");
        usernameLabel.setBounds(10, 110, 80, 25);
        panel.add(usernameLabel);

        JTextField usernameText = new JTextField(20);
        usernameText.setBounds(150, 110, 165, 25);
        panel.add(usernameText);

        JLabel passwordLabel = new JLabel("Parolă:");
        passwordLabel.setBounds(10, 140, 80, 25);
        panel.add(passwordLabel);

        JPasswordField passwordText = new JPasswordField(20);
        passwordText.setBounds(150, 140, 165, 25);
        panel.add(passwordText);

        JButton addButton = new JButton("Creează/Editează Elev");
        addButton.setBounds(10, 170, 200, 25);
        panel.add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru adăugarea/editarea unui elev
            }
        });

        JLabel listTitleLabel = new JLabel("Lista Elevilor");
        listTitleLabel.setBounds(10, 200, 300, 25);
        panel.add(listTitleLabel);

        JTable studentsTable = new JTable(new String[][]{
                {"Student 1", "10A", "student1"}
        }, new String[]{"Nume", "Grupă", "Nume Utilizator"});
        JScrollPane studentsScrollPane = new JScrollPane(studentsTable);
        studentsScrollPane.setBounds(10, 230, 760, 200);
        panel.add(studentsScrollPane);

        JButton editButton = new JButton("Edit");
        editButton.setBounds(10, 440, 80, 25);
        panel.add(editButton);
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru editarea unui elev
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(100, 440, 80, 25);
        panel.add(deleteButton);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru ștergerea unui elev
            }
        });
    }
}
