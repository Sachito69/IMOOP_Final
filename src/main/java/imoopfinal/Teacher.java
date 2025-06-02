package imoopfinal;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.text.DateFormatSymbols;
import java.sql.Date;
import java.sql.Time;


public class Teacher extends JFrame {
    private JTextField teacherNameField;
    private JTextField departmentField;
    private JComboBox<String> yearComboBox, monthComboBox, dayComboBox, slotComboBox;
    private JComboBox<String> startHourComboBox, startMinuteComboBox;
    private JComboBox<String> endHourComboBox, endMinuteComboBox;
    private JComboBox<String> studentFilterComboBox, yearFilterComboBox, monthFilterComboBox, courseFilterComboBox;
    private JTable scheduleTable;
    private DefaultTableModel scheduleTableModel;
    private JTable appointmentTable;
    private DefaultTableModel appointmentTableModel;

public Teacher(String firstName, String lastName, String department) {
    setTitle("Teacher Schedule Management");
    setSize(630, 750);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    // Teacher Name
    JLabel teacherNameLabel = new JLabel("Teacher Name:");
    gbc.gridx = 0; gbc.gridy = 0;
    panel.add(teacherNameLabel, gbc);

    teacherNameField = new JTextField(firstName + " " + lastName);
    teacherNameField.setEditable(false);
    gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 5;
    panel.add(teacherNameField, gbc);

    // Department
    JLabel departmentLabel = new JLabel("Department:");
    gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
    panel.add(departmentLabel, gbc);

    departmentField = new JTextField(department);
    departmentField.setEditable(false);
    gbc.gridx = 1; gbc.gridy = 1; gbc.gridwidth = 5;
    panel.add(departmentField, gbc);

    // Schedule Label
    JLabel scheduleLabel = new JLabel("Schedule:");
    gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 6;
    gbc.anchor = GridBagConstraints.WEST;
    panel.add(scheduleLabel, gbc);

    // Schedule Table
    scheduleTableModel = new DefaultTableModel(new Object[]{"Date", "Time", "Slots"}, 0);
    scheduleTable = new JTable(scheduleTableModel);
    JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
    scheduleScrollPane.setPreferredSize(new Dimension(600, 180));
    gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 6;
    panel.add(scheduleScrollPane, gbc);

    // Add & Delete Schedule Buttons
    JButton addScheduleButton = new JButton("Add Schedule");
    JButton deleteButton = new JButton("Delete Schedule");

    gbc.gridy = 4; gbc.gridwidth = 2; gbc.gridx = 1;
    panel.add(addScheduleButton, gbc);

    gbc.gridx = 3; gbc.gridwidth = 1;gbc.gridx = 4;
    panel.add(deleteButton, gbc);

    // Appointments Label
    JLabel appointmentLabel = new JLabel("Appointments:");
    gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 6;
    panel.add(appointmentLabel, gbc);

    // Appointment Filters
    studentFilterComboBox = new JComboBox<>();
    studentFilterComboBox.addItem("All Students");
    gbc.gridx = 0; gbc.gridwidth = 1; gbc.gridy = 6;
    panel.add(studentFilterComboBox, gbc);

    courseFilterComboBox = new JComboBox<>();
    courseFilterComboBox.addItem("All Courses");
    courseFilter();
    gbc.gridx = 1; gbc.gridwidth = 1;gbc.gridy = 6;
    panel.add(courseFilterComboBox, gbc);

    yearFilterComboBox = new JComboBox<>();
    yearFilterComboBox.addItem("All Years");
    for (int year = 2024; year <= 2030; year++) {
        yearFilterComboBox.addItem(String.valueOf(year));
    }
    gbc.gridx = 3; gbc.gridwidth = 1;gbc.gridy = 6;
    panel.add(yearFilterComboBox, gbc);

    monthFilterComboBox = new JComboBox<>();
    monthFilterComboBox.addItem("All Months");
    String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    for (String month : months) monthFilterComboBox.addItem(month);
    gbc.gridx = 5;gbc.gridwidth = 1; gbc.gridy = 6;
    panel.add(monthFilterComboBox, gbc);

    // Appointment Table
    appointmentTableModel = new DefaultTableModel(new Object[]{"Student", "Course", "Date", "Time"}, 0);
    appointmentTable = new JTable(appointmentTableModel);
    JScrollPane appointmentScrollPane = new JScrollPane(appointmentTable);
    appointmentScrollPane.setPreferredSize(new Dimension(600, 180));
    gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 6;
    panel.add(appointmentScrollPane, gbc);

    // Logout Button (centered)
    JButton logoutButton = new JButton("Logout");
    gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 6;
    gbc.anchor = GridBagConstraints.CENTER;
    panel.add(logoutButton, gbc);

    add(panel);

    // Event Bindings
    initializeComboBoxes();

    addScheduleButton.addActionListener(e -> showDateSlotDialog());
    deleteButton.addActionListener(e -> deleteSelectedSchedule());
    logoutButton.addActionListener(e -> {
        int response = JOptionPane.showConfirmDialog(Teacher.this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            dispose();
            new Login().setVisible(true);
        }
    });

    studentFilterComboBox.addActionListener(e -> loadAppointments());
    yearFilterComboBox.addActionListener(e -> loadAppointments());
    monthFilterComboBox.addActionListener(e -> loadAppointments());
    courseFilterComboBox.addActionListener(e -> loadAppointments());

    loadStudents();
    loadSchedule();  // ✅ this loads slots from database correctly
    loadAppointments();
}

    private void showDateSlotDialog() {
        JPanel dialogPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        dialogPanel.add(new JLabel("Year:")); dialogPanel.add(yearComboBox);
        dialogPanel.add(new JLabel("Month:")); dialogPanel.add(monthComboBox);
        dialogPanel.add(new JLabel("Day:")); dialogPanel.add(dayComboBox);
        dialogPanel.add(new JLabel("# of Slots:")); dialogPanel.add(slotComboBox);
        dialogPanel.add(new JLabel(" Time:"));
        dialogPanel.add(new JPanel(new FlowLayout(FlowLayout.LEFT)) {{
            add(startHourComboBox);
            add(new JLabel(":"));
            add(startMinuteComboBox);
        }});

        int result = JOptionPane.showConfirmDialog(
                this, dialogPanel, "Select Date, Slot, and Time", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            addSchedule();
        }
    }

    private void addSchedule() {
    String teacherName = teacherNameField.getText().trim();
    String departmentValue = departmentField.getText().trim();

    String monthName = (String) monthComboBox.getSelectedItem();
    String day = (String) dayComboBox.getSelectedItem();
    String year = (String) yearComboBox.getSelectedItem();

    DateFormatSymbols dfs = new DateFormatSymbols();
    String[] months = dfs.getMonths();
    int monthNumber = 0;
    for (int i = 0; i < months.length; i++) {
        if (months[i].equalsIgnoreCase(monthName)) {
            monthNumber = i + 1;
            break;
        }
    }

    String monthPadded = String.format("%02d", monthNumber);
    String dayPadded = String.format("%02d", Integer.parseInt(day));
    String sqlDate = year + "-" + monthPadded + "-" + dayPadded;

    String time = startHourComboBox.getSelectedItem() + ":" + startMinuteComboBox.getSelectedItem();
    String selectedYear = year;
    String selectedMonth = monthName;
    String numberOfSlots = (String) slotComboBox.getSelectedItem();

    try (Connection conn = IMOOPFinal.getConnection()) {

        // Check for duplicate schedule
        String checkSql = "SELECT COUNT(*) FROM schedule WHERE teacher = ? AND date = ? AND time = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setString(1, teacherName);
            checkStmt.setDate(2, Date.valueOf(sqlDate));
            checkStmt.setTime(3, Time.valueOf(time + ":00"));
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, "Schedule already exists for this teacher at the selected date and time.", "Duplicate", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }

        // Insert schedule
        String sql = "INSERT INTO schedule (teacher, department, date, time, year, month, slots) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, teacherName);
            pstmt.setString(2, departmentValue);
            pstmt.setDate(3, Date.valueOf(sqlDate));
            pstmt.setTime(4, Time.valueOf(time + ":00"));
            pstmt.setString(5, selectedYear);
            pstmt.setString(6, selectedMonth);
            pstmt.setInt(7, Integer.parseInt(numberOfSlots));

            pstmt.executeUpdate();
            loadSchedule();
            JOptionPane.showMessageDialog(this, "Schedule added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error adding schedule: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        System.err.println("SQL Error: " + ex.getMessage());
    }
}

    private void initializeComboBoxes() {
        yearComboBox = new JComboBox<>();
        for (int year = 2024; year <= 2030; year++) yearComboBox.addItem(String.valueOf(year));

        monthComboBox = new JComboBox<>();
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        for (String month : months) monthComboBox.addItem(month);

        dayComboBox = new JComboBox<>();
        for (int day = 1; day <= 31; day++) dayComboBox.addItem(String.format("%02d", day));

        slotComboBox = new JComboBox<>();
        for (int i = 1; i <= 5; i++) { slotComboBox.addItem(String.valueOf(i));}
        
        startHourComboBox = new JComboBox<>();
        endHourComboBox = new JComboBox<>();
        for (int i = 0; i < 24; i++) {
            startHourComboBox.addItem(String.format("%02d", i));
            endHourComboBox.addItem(String.format("%02d", i));
        }

        startMinuteComboBox = new JComboBox<>();
        endMinuteComboBox = new JComboBox<>();
        for (int i = 0; i < 60; i++) {
            startMinuteComboBox.addItem(String.format("%02d", i));
            endMinuteComboBox.addItem(String.format("%02d", i));
        }
    }

    private void deleteSelectedSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a schedule to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String date = (String) scheduleTableModel.getValueAt(selectedRow, 0);
        String time = (String) scheduleTableModel.getValueAt(selectedRow, 1);

        try (Connection conn = IMOOPFinal.getConnection()) {
            String sql = "DELETE FROM schedule WHERE date = ? AND time = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, date);
                pstmt.setString(2, time);
                int rowsDeleted = pstmt.executeUpdate();
                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "Schedule deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadSchedule();
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting schedule: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSchedule() {
        scheduleTableModel.setRowCount(0);
        try (Connection conn = IMOOPFinal.getConnection()) {
            String sql = "SELECT DISTINCT date, time, slots FROM schedule WHERE teacher = ?";
            String teacherName = teacherNameField.getText().trim();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, teacherName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String date = rs.getString("date");
                        String time = rs.getString("time");
                        int slots = rs.getInt("slots");
                        scheduleTableModel.addRow(new Object[]{date, time, slots});
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
        StringBuilder sql = new StringBuilder(
            "SELECT student, course, date, time FROM appointments a " +
            "LEFT JOIN users u ON student = username WHERE teacher = ?"
        );

        String teacherName = teacherNameField.getText().trim();
        boolean hasStudentFilter = studentFilterComboBox.getSelectedItem() != null &&
                !studentFilterComboBox.getSelectedItem().equals("All Students");
        boolean hasYearFilter = yearFilterComboBox.getSelectedItem() != null &&
                !yearFilterComboBox.getSelectedItem().equals("All Years");
        boolean hasMonthFilter = monthFilterComboBox.getSelectedItem() != null &&
                !monthFilterComboBox.getSelectedItem().equals("All Months");
        boolean hasCourseFilter = courseFilterComboBox.getSelectedItem() != null &&
                !courseFilterComboBox.getSelectedItem().equals("All Courses");

        if (hasStudentFilter) sql.append(" AND student = ?");
        if (hasYearFilter) sql.append(" AND a.year = ?");
        if (hasMonthFilter) sql.append(" AND a.month = ?");
        if (hasCourseFilter) sql.append(" AND course = ?");

        try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            pstmt.setString(paramIndex++, teacherName);

            if (hasStudentFilter)
                pstmt.setString(paramIndex++, (String) studentFilterComboBox.getSelectedItem());
            if (hasYearFilter)
                pstmt.setString(paramIndex++, (String) yearFilterComboBox.getSelectedItem());
            if (hasMonthFilter)
                pstmt.setString(paramIndex++, (String) monthFilterComboBox.getSelectedItem());
            if (hasCourseFilter)
                pstmt.setString(paramIndex++, (String) courseFilterComboBox.getSelectedItem());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String student = rs.getString("student");
                    String course = rs.getString("course");
                    String date = rs.getString("date");
                    String time = rs.getString("time");
                    appointmentTableModel.addRow(new Object[]{student, course, date, time});
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading appointments: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void loadStudents() {
    studentFilterComboBox.removeAllItems(); // Clear existing items
    studentFilterComboBox.addItem("All Students");

    try (Connection conn = IMOOPFinal.getConnection()) {
        String sql = "SELECT DISTINCT student FROM appointments";
        try (PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String student = rs.getString("student");
                studentFilterComboBox.addItem(student);
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Error loading students: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private void courseFilter() {
        courseFilterComboBox.removeAllItems(); // Clear existing items
        courseFilterComboBox.addItem("All Courses");

        try (Connection conn = IMOOPFinal.getConnection()) {
            String sql = "SELECT DISTINCT course FROM appointments";
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String course = rs.getString("course");
                    courseFilterComboBox.addItem(course);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading courses: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Teacher("John", "Doe", "Dep1").setVisible(true);
            }
        });
    }
}
