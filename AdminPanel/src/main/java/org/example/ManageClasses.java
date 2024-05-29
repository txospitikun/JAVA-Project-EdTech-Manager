package org.example;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManageClasses {
    public ManageClasses() {
        JFrame frame = new JFrame("Admin Panel - Gestionează Grupe");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        frame.setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Gestionează Grupe");
        titleLabel.setBounds(10, 20, 300, 25);
        panel.add(titleLabel);

        JLabel nameLabel = new JLabel("Nume Grupă:");
        nameLabel.setBounds(10, 50, 80, 25);
        panel.add(nameLabel);

        JTextField nameText = new JTextField(20);
        nameText.setBounds(150, 50, 165, 25);
        panel.add(nameText);

        JLabel yearLabel = new JLabel("An de studiu:");
        yearLabel.setBounds(10, 80, 80, 25);
        panel.add(yearLabel);

        JTextField yearText = new JTextField(20);
        yearText.setBounds(150, 80, 165, 25);
        panel.add(yearText);

        JLabel subjectsLabel = new JLabel("Materii:");
        subjectsLabel.setBounds(10, 110, 80, 25);
        panel.add(subjectsLabel);

        JList<String> subjectsList = new JList<>(new String[]{"Matematică", "Fizică", "Informatică"});
        subjectsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane subjectsScrollPane = new JScrollPane(subjectsList);
        subjectsScrollPane.setBounds(150, 110, 165, 75);
        panel.add(subjectsScrollPane);

        JLabel teachersLabel = new JLabel("Profesori:");
        teachersLabel.setBounds(10, 190, 80, 25);
        panel.add(teachersLabel);

        JTextField teachersText = new JTextField(20);
        teachersText.setBounds(150, 190, 165, 25);
        panel.add(teachersText);

        JLabel studentsLabel = new JLabel("Elevi:");
        studentsLabel.setBounds(10, 220, 80, 25);
        panel.add(studentsLabel);

        JTextField studentsText = new JTextField(20);
        studentsText.setBounds(150, 220, 165, 25);
        panel.add(studentsText);

        JButton addButton = new JButton("Creează/Editează Grupă");
        addButton.setBounds(10, 250, 200, 25);
        panel.add(addButton);
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru adăugarea/editarea unei grupe
            }
        });

        JLabel listTitleLabel = new JLabel("Lista Grupelor");
        listTitleLabel.setBounds(10, 280, 300, 25);
        panel.add(listTitleLabel);

        JTable classesTable = new JTable(new String[][]{
                {"10A", "10", "Matematică (3 ore), Fizică (2 ore)", "Prof. Popescu, Prof. Ionescu", "Student 1, Student 2"}
        }, new String[]{"Nume Grupă", "An de studiu", "Materii", "Profesori", "Elevi"});
        JScrollPane classesScrollPane = new JScrollPane(classesTable);
        classesScrollPane.setBounds(10, 310, 760, 200);
        panel.add(classesScrollPane);

        JButton editButton = new JButton("Edit");
        editButton.setBounds(10, 520, 80, 25);
        panel.add(editButton);
        editButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru editarea unei grupe
            }
        });

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(100, 520, 80, 25);
        panel.add(deleteButton);
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Logica pentru ștergerea unei grupe
            }
        });
    }
}
