package imoopfinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUp extends JFrame {
    private JTextField signUpUsernameField;
    private JPasswordField signUpPasswordField;
    private JTextField emailField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JRadioButton teacherRadioButton;
    private JRadioButton studentRadioButton;
    private JComboBox<String> departmentComboBox;

    public SignUp() {
        setTitle("Sign Up");
        setSize(400, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        panel.add(new JLabel("Username:"), gbcAt(gbc, 0, row));
        signUpUsernameField = new JTextField();
        panel.add(signUpUsernameField, gbcAt(gbc, 1, row++));

        panel.add(new JLabel("Password:"), gbcAt(gbc, 0, row));
        signUpPasswordField = new JPasswordField();
        panel.add(signUpPasswordField, gbcAt(gbc, 1, row++));

        panel.add(new JLabel("Email:"), gbcAt(gbc, 0, row));
        emailField = new JTextField();
        panel.add(emailField, gbcAt(gbc, 1, row++));

        panel.add(new JLabel("First Name:"), gbcAt(gbc, 0, row));
        firstNameField = new JTextField();
        panel.add(firstNameField, gbcAt(gbc, 1, row++));

        panel.add(new JLabel("Last Name:"), gbcAt(gbc, 0, row));
        lastNameField = new JTextField();
        panel.add(lastNameField, gbcAt(gbc, 1, row++));

        panel.add(new JLabel("User Type:"), gbcAt(gbc, 0, row));
        teacherRadioButton = new JRadioButton("Teacher");
        studentRadioButton = new JRadioButton("Student");
        ButtonGroup group = new ButtonGroup();
        group.add(teacherRadioButton);
        group.add(studentRadioButton);
        JPanel typePanel = new JPanel();
        typePanel.add(teacherRadioButton);
        typePanel.add(studentRadioButton);
        panel.add(typePanel, gbcAt(gbc, 1, row++));

        panel.add(new JLabel("Department:"), gbcAt(gbc, 0, row));
        departmentComboBox = new JComboBox<>(new String[]{"CCS", "Fine Arts", "Department 3", "Department 4"});
        panel.add(departmentComboBox, gbcAt(gbc, 1, row++));

        JButton signUpButton = new JButton("Sign Up");
        panel.add(signUpButton, gbcAt(gbc, 1, row));

        JButton cancelButton = new JButton("Cancel");
        panel.add(cancelButton, gbcAt(gbc, 0, row++));

        add(panel);

        cancelButton.addActionListener(e -> {
            dispose();
            new Login().setVisible(true);
        });

        signUpButton.addActionListener(e -> {
            String username = signUpUsernameField.getText().trim();
            String password = new String(signUpPasswordField.getPassword()).trim();
            String email = emailField.getText().trim();
            String first = firstNameField.getText().trim();
            String last = lastNameField.getText().trim();
            String userType = teacherRadioButton.isSelected() ? "Teacher" : studentRadioButton.isSelected() ? "Student" : "";
            String department = (String) departmentComboBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty() || email.isEmpty() || first.isEmpty() || last.isEmpty() || userType.isEmpty()) {
                JOptionPane.showMessageDialog(SignUp.this, "All fields must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = IMOOPFinal.getConnection()) {
                String sql = "INSERT INTO users (username, password, email, firstname, lastname, department, usertype) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, username);
                    stmt.setString(2, password);
                    stmt.setString(3, email);
                    stmt.setString(4, first);
                    stmt.setString(5, last);
                    stmt.setString(6, department);
                    stmt.setString(7, userType);
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(SignUp.this, "Sign up successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                    new Login().setVisible(true);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(SignUp.this, "Sign up failed. Username may already exist.\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private GridBagConstraints gbcAt(GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        return gbc;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp().setVisible(true));
    }
}
