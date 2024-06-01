
package org.example.proiectjava.controller;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class ManageStudents extends Frame {
    private TextField studentNameField, studentGroupField, studentUsernameField, studentPasswordField;
    private Button createEditButton;
    private JTable studentsTable;
    private StudentsTableModel tableModel;

    public ManageStudents() {
        setTitle("Admin Panel - Gestionează Elevi");
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Header
        Panel header = new Panel(new FlowLayout(FlowLayout.CENTER));
        String[] navItems = {"Home", "Gestionează Profesori", "Gestionează Elevi", "Gestionează Grupe", "Gestionează Orar", "Gestionează Anunțuri", "Logout"};
        for (String item : navItems) {
            Button navButton = new Button(item);
            header.add(navButton);
        }
        add(header, BorderLayout.NORTH);

        // Main content
        Panel mainPanel = new Panel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Form
        Panel formPanel = new Panel(new GridLayout(5, 2));
        formPanel.add(new Label("Nume:"));
        studentNameField = new TextField();
        formPanel.add(studentNameField);

        formPanel.add(new Label("Grupă:"));
        studentGroupField = new TextField();
        formPanel.add(studentGroupField);

        formPanel.add(new Label("Nume utilizator:"));
        studentUsernameField = new TextField();
        formPanel.add(studentUsernameField);

        formPanel.add(new Label("Parolă:"));
        studentPasswordField = new TextField();
        formPanel.add(studentPasswordField);

        createEditButton = new Button("Creează/Editează Elev");
        createEditButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOrEditStudent();
            }
        });
        formPanel.add(createEditButton);
        mainPanel.add(formPanel);

        // Table
        tableModel = new StudentsTableModel();
        studentsTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(studentsTable);
        mainPanel.add(tableScrollPane);

        add(mainPanel, BorderLayout.CENTER);

        // Footer
        Panel footer = new Panel();
        footer.add(new Label("© 2024 Școala XYZ"));
        add(footer, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void addOrEditStudent() {
        String name = studentNameField.getText();
        String group = studentGroupField.getText();
        String username = studentUsernameField.getText();
        String password = studentPasswordField.getText();

        if (!name.isEmpty() && !group.isEmpty() && !username.isEmpty() && !password.isEmpty()) {
            tableModel.addStudent(new Student(name, group, username));
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new ManageStudents();
    }

    class Student {
        String name;
        String group;
        String username;

        public Student(String name, String group, String username) {
            this.name = name;
            this.group = group;
            this.username = username;
        }
    }

    class StudentsTableModel extends AbstractTableModel {
        private final String[] columnNames = {"Nume", "Grupă", "Nume Utilizator", "Acțiuni"};
        private final ArrayList<Student> students = new ArrayList<>();

        public void addStudent(Student student) {
            students.add(student);
            fireTableRowsInserted(students.size() - 1, students.size() - 1);
        }

        @Override
        public int getRowCount() {
            return students.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            Student student = students.get(row);
            switch (col) {
                case 0: return student.name;
                case 1: return student.group;
                case 2: return student.username;
                case 3: return "Edit/Delete"; // Placeholder for action buttons
                default: return null;
            }
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 3; // Only the action buttons column is editable
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            // Handle cell edits if necessary
        }
    }
}