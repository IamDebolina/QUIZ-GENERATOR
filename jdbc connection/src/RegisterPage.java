import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPage extends JFrame {
    private JTextField usernameField, emailField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;

    public RegisterPage() {
        setTitle("Register");
        setSize(600, 450);  // Increased size
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60)); // More padding
        mainPanel.setBackground(new Color(230, 240, 250));

        JLabel titleLabel = new JLabel("Register");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30)); // Larger font
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(mainPanel.getBackground());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // More spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.PLAIN, 18);
        Font fieldFont = new Font("Arial", Font.PLAIN, 16);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        formPanel.add(userLabel, gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(fieldFont);
        formPanel.add(usernameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(fieldFont);
        formPanel.add(emailField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        formPanel.add(passLabel, gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        formPanel.add(passwordField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(mainPanel.getBackground());

        registerButton = new JButton("Register");
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setBackground(new Color(70, 130, 180));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(150, 40));

        backButton = new JButton("Back to Login");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(new Color(100, 149, 237));
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.setPreferredSize(new Dimension(150, 40));

        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(backButton);

        // Action Listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginPage().setVisible(true);
            }
        });
        

        // Add all components
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void registerUser() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password); // Should hash in production
            stmt.setString(3, email);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.");
            dispose();
            new LoginPage().setVisible(true);

        } catch (SQLIntegrityConstraintViolationException ex) {
            JOptionPane.showMessageDialog(this, "Username already exists.");
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration failed.");
        }
    }

    public static void main(String[] args) {
        new RegisterPage();
    }
}
