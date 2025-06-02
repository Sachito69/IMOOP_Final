package imoopfinal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {
        setTitle("FU Student-Teacher Appointment Managing Program");
        setSize(500, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Load logo (make sure logo.png is in the resources folder)
        JLabel logoLabel;
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/logo.png"));
            logoLabel = new JLabel(logoIcon);
        } catch (Exception e) {
            logoLabel = new JLabel("FU STAMP");
            System.out.println("Logo not found: " + e.getMessage());
        }

        JLabel textLabel = new JLabel("Welcome to FU STAMP!");
        textLabel.setFont(new Font("Arial", Font.BOLD, 22));
        textLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        logoPanel.add(textLabel, BorderLayout.SOUTH);
        panel.add(logoPanel, gbc);

        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(usernameLabel, gbc);

        usernameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        usernameField.setPreferredSize(new Dimension(200, 25));
        panel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        passwordField.setPreferredSize(new Dimension(200, 25));
        panel.add(passwordField, gbc);

        JButton loginButton = new JButton("Login");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        JButton signUpButton = new JButton("Sign Up");
        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(signUpButton, gbc);

        add(panel);

        loginButton.addActionListener((ActionEvent e) -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(Login.this, "Username and password must be filled.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = IMOOPFinal.getConnection()) {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            String userType = rs.getString("usertype");
                            String firstName = rs.getString("firstname");
                            String lastName = rs.getString("lastname");
                            String department = rs.getString("department");

                            if ("Teacher".equalsIgnoreCase(userType)) {
                                System.out.println("Opening Teacher UI...");
                                new Teacher(firstName, lastName, department).setVisible(true);
                            } else if ("Student".equalsIgnoreCase(userType)) {
                                System.out.println("Opening Student UI...");
                                new Student(firstName, lastName, department).setVisible(true);
                            } else {
                                System.out.println("Unknown user type: " + userType);
                            }

                            dispose(); // Close login window
                        } else {
                            JOptionPane.showMessageDialog(Login.this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(Login.this, "Error logging in: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        signUpButton.addActionListener(e -> {
            dispose();
            new SignUp().setVisible(true);
        });
    }
}
