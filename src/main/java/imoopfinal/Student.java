package imoopfinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public class Student extends JFrame {
    private JTextField studentNameField;
    private JTextField departmentField;
    private JComboBox<String> courseComboBox;
    private JComboBox<String> teacherComboBox;
    private JTable scheduleTable;
    private DefaultTableModel scheduleTableModel;
    private JTable appointmentTable;
    private DefaultTableModel appointmentTableModel;
    private Map<String, String[]> departmentCoursesMap;

    public Student(String firstName, String lastName, String department) {
        setTitle("Student Appointment Booking");
        setSize(630, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true); // you could add this here too

        // Initialize the department-courses map
        departmentCoursesMap = new HashMap<>();
        departmentCoursesMap.put("CCS", new String[]{"Networking", "Programming", "IT122", "sachoiscool"});
        departmentCoursesMap.put("Fine Arts", new String[]{"Drawing", "Painting","Photography","sculpture"});
        departmentCoursesMap.put("Department 3", new String[]{"Course 3.1", "Course 3.2", "Course 3.3"});
        departmentCoursesMap.put("Department 4", new String[]{"Course 4.1", "Course 4.2", "Course 4.3"});

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Student name field
        JLabel studentNameLabel = new JLabel("Name:");
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(studentNameLabel, gbc);

        studentNameField = new JTextField(firstName + " " + lastName);
        studentNameField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(studentNameField, gbc);

        // Department field
        JLabel departmentLabel = new JLabel("Department:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(departmentLabel, gbc);

        departmentField = new JTextField(department);
        departmentField.setEditable(false);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(departmentField, gbc);

        // Course combo box
        JLabel courseLabel = new JLabel("Course:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(courseLabel, gbc);

        courseComboBox = new JComboBox<>();
        courseComboBox.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(courseComboBox, gbc);

        // Populate the course combo box based on the initial department
        updateCourseComboBox(department);  // This should add other items but "Choose a Course" stays selected

        // Teacher combo box
        JLabel teacherLabel = new JLabel("Teacher:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(teacherLabel, gbc);

        teacherComboBox = new JComboBox<>();
        teacherComboBox.setPreferredSize(new Dimension(150, 25));
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(teacherComboBox, gbc);


        JLabel scheduleLabel = new JLabel("Teacher's Schedule:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(scheduleLabel, gbc);

        scheduleTableModel = new DefaultTableModel(new Object[]{"Date", "Time", "Available Slots"}, 0);
        scheduleTable = new JTable(scheduleTableModel);
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
        scheduleScrollPane.setPreferredSize(new Dimension(500, 180));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(scheduleScrollPane, gbc);
        
        JButton bookButton = new JButton("Book Appointment");
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        panel.add(bookButton, gbc);

        // Appointments label and table
        JLabel appointmentLabel = new JLabel("Appointments:");
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        panel.add(appointmentLabel, gbc);

        appointmentTableModel = new DefaultTableModel(new Object[]{"Teacher", "Date", "Time"}, 0);
        appointmentTable = new JTable(appointmentTableModel);
        JScrollPane appointmentScrollPane = new JScrollPane(appointmentTable);
        appointmentScrollPane.setPreferredSize(new Dimension(500, 180));
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(appointmentScrollPane, gbc);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        panel.add(logoutButton, gbc);

        // Delete appointment button
        JButton deleteButton = new JButton("Delete Appointment");
        gbc.gridx = 1;
        gbc.gridy = 9;
        gbc.gridwidth = 1;
        panel.add(deleteButton, gbc);

        add(panel);

        // Book button action listener
    bookButton.addActionListener(new ActionListener() {
    @Override
    public void actionPerformed(ActionEvent e) {
        String studentName = studentNameField.getText().trim();
        String teacherName = (String) teacherComboBox.getSelectedItem();
        int selectedRow = scheduleTable.getSelectedRow();

        if (teacherName == null || selectedRow == -1) {
            JOptionPane.showMessageDialog(Student.this, "Please select a time slot to book an appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String date = (String) scheduleTableModel.getValueAt(selectedRow, 0);
        String time = (String) scheduleTableModel.getValueAt(selectedRow, 1);
        String course = (String) courseComboBox.getSelectedItem();

        try (Connection conn = IMOOPFinal.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Check if appointment already exists
            String checkQuery = "SELECT * FROM appointments WHERE student = ? AND teacher = ? AND date = ? AND time = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, studentName);
                checkStmt.setString(2, teacherName);
                checkStmt.setString(3, date);
                checkStmt.setString(4, time);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(Student.this, "You already have an appointment for this slot.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            }

            // Get year and month from schedule
            String year = null, month = null;
            String query = "SELECT year, month FROM schedule WHERE teacher = ? AND date = ? AND time = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, teacherName);
                pstmt.setString(2, date);
                pstmt.setString(3, time);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        year = rs.getString("year");
                        month = rs.getString("month");
                    }
                }
            }

            if (year == null || month == null) {
                JOptionPane.showMessageDialog(Student.this, "Error retrieving year and month information.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert appointment
            String insertSQL = "INSERT INTO appointments (student, teacher, date, time, year, month, course) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                insertStmt.setString(1, studentName);
                insertStmt.setString(2, teacherName);
                insertStmt.setString(3, date);
                insertStmt.setString(4, time);
                insertStmt.setString(5, year);
                insertStmt.setString(6, month);
                insertStmt.setString(7, course);
                insertStmt.executeUpdate();
            }

            // Update slots in schedule table
            String updateSlotsSQL = "UPDATE schedule SET slots = slots - 1 WHERE teacher = ? AND date = ? AND time = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSlotsSQL)) {
                updateStmt.setString(1, teacherName);
                updateStmt.setString(2, date);
                updateStmt.setString(3, time);
                updateStmt.executeUpdate();
            }

            conn.commit(); // Commit transaction
            JOptionPane.showMessageDialog(Student.this, "Appointment booked successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAppointments();
            loadSchedule();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(Student.this, "Error booking appointment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
});

        // Teacher combo box action listener
        teacherComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSchedule();
            }
        });

        // Logout button action listener
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(Student.this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                    new Login().setVisible(true);
                }
            }
        });

        // Delete button action listener
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = appointmentTable.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(Student.this, "Please select an appointment to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String teacher = (String) appointmentTableModel.getValueAt(selectedRow, 0);
                String date = (String) appointmentTableModel.getValueAt(selectedRow, 1);
                String time = (String) appointmentTableModel.getValueAt(selectedRow, 2);

                String year = null;
                String month = null;

                try (Connection conn = IMOOPFinal.getConnection()) {
                    String query = "SELECT year, month FROM appointments WHERE student = ? AND teacher = ? AND date = ? AND time = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                        pstmt.setString(1, studentNameField.getText().trim());
                        pstmt.setString(2, teacher);
                        pstmt.setString(3, date);
                        pstmt.setString(4, time);
                        try (ResultSet rs = pstmt.executeQuery()) {
                            if (rs.next()) {
                                year = rs.getString("year");
                                month = rs.getString("month");
                            }
                        }
                    }

                    if (year == null || month == null) {
                        JOptionPane.showMessageDialog(Student.this, "Error retrieving year and month information.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String sql = "DELETE FROM appointments WHERE student = ? AND teacher = ? AND date = ? AND time = ? AND year = ? AND month = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setString(1, studentNameField.getText().trim());
                        pstmt.setString(2, teacher);
                        pstmt.setString(3, date);
                        pstmt.setString(4, time);
                        pstmt.setString(5, year);
                        pstmt.setString(6, month);

                        int rowsDeleted = pstmt.executeUpdate();
                        if (rowsDeleted > 0) {
                            JOptionPane.showMessageDialog(Student.this, "Appointment deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                            loadAppointments();
                        } else {
                            JOptionPane.showMessageDialog(Student.this, "No appointment found to delete.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Student.this, "Error deleting appointment: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Populate the initial data
        loadTeachers(department);
        loadSchedule();
        loadAppointments();
    }

    private void updateCourseComboBox(String department) {
    courseComboBox.removeAllItems();
    courseComboBox.addItem("Choose a Course"); // Add prompt as first item

    String[] courses = departmentCoursesMap.get(department);
    if (courses != null) {
        for (String course : courses) {
            courseComboBox.addItem(course);
        }
    }

    courseComboBox.setSelectedIndex(0); // Ensure the prompt stays selected
}

    private void loadTeachers(String studentDepartment) {
    teacherComboBox.removeAllItems();
    teacherComboBox.addItem("Choose a Teacher"); // Add default prompt

    try (Connection conn = IMOOPFinal.getConnection()) {
        String sql = "SELECT DISTINCT teacher FROM schedule WHERE department = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentDepartment); // Set the department parameter
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    teacherComboBox.addItem(rs.getString("teacher"));
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading teachers: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    teacherComboBox.setSelectedIndex(0); // Keep the prompt selected
}

    private void loadSchedule() {
    scheduleTableModel.setRowCount(0);
    try (Connection conn = IMOOPFinal.getConnection()) {
        String sql = "SELECT s.date, s.time, s.year, s.month, s.slots, " +
                     "(SELECT COUNT(*) FROM appointments a WHERE a.teacher = s.teacher AND a.date = s.date AND a.time = s.time) AS booked " +
                     "FROM schedule s WHERE s.teacher = ? AND s.slots > 0";
        
        String selectedTeacher = (String) teacherComboBox.getSelectedItem();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, selectedTeacher);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    int booked = rs.getInt("booked");
                    int availableSlots = rs.getInt("slots"); // you can use this directly if you want raw slots

                    // If you still want to show how many more can be booked (e.g., based on a limit like 5), you could do:
                    // int availableSlots = rs.getInt("slots") - booked;

                    scheduleTableModel.addRow(new Object[]{date, time, availableSlots});
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading schedule: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void loadAppointments() {
        appointmentTableModel.setRowCount(0);
        try (Connection conn = IMOOPFinal.getConnection()) {
            String sql = "SELECT DISTINCT teacher, date, time, year, month FROM appointments WHERE student = ?";
            String studentName = studentNameField.getText().trim();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, studentName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String teacher = rs.getString("teacher");
                        String date = rs.getString("date");
                        String time = rs.getString("time");
                        appointmentTableModel.addRow(new Object[]{teacher, date, time});
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading appointments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Student("John", "Doe", "CCS").setVisible(true));
    }
}
