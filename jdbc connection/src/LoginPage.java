import javax.swing.*; 
import java.awt.*; 
import java.awt.event.*; 
import java.sql.*; 
public class LoginPage extends JFrame { 
private JTextField usernameField; 
private JPasswordField passwordField; 
private JButton loginButton, cancelButton, signupButton; 
public LoginPage() { 
setTitle("Login Page"); 
setSize(600, 400); // Slightly smaller after removing dropdown 
setDefaultCloseOperation(EXIT_ON_CLOSE); 
setLocationRelativeTo(null); 
setResizable(false); 
// Main panel setup 
JPanel mainPanel = new JPanel(new GridBagLayout()); 
mainPanel.setBackground(new Color(222, 233, 250)); 
GridBagConstraints gbc = new GridBagConstraints(); 
gbc.insets = new Insets(15, 15, 15, 15); 
gbc.anchor = GridBagConstraints.WEST; 
// Title 
JLabel titleLabel = new JLabel("Login Page"); 
titleLabel.setFont(new Font("Arial", Font.BOLD, 32)); 
gbc.gridx = 1; 
gbc.gridy = 0; 
gbc.gridwidth = 2; 
gbc.anchor = GridBagConstraints.CENTER; 
mainPanel.add(titleLabel, gbc); 
Font labelFont = new Font("Arial", Font.PLAIN, 18); 
Font fieldFont = new Font("Arial", Font.PLAIN, 16); 
// Username 
gbc.gridwidth = 1; 
gbc.anchor = GridBagConstraints.WEST; 
gbc.gridx = 0; 
gbc.gridy = 1; 
JLabel userLabel = new JLabel("Username:"); 
userLabel.setFont(labelFont); 
mainPanel.add(userLabel, gbc); 
gbc.gridx = 1; 
usernameField = new JTextField(20); 
usernameField.setFont(fieldFont); 
mainPanel.add(usernameField, gbc); 
// Password 
gbc.gridx = 0; 
gbc.gridy = 2; 
JLabel passLabel = new JLabel("Password:"); 
passLabel.setFont(labelFont); 
mainPanel.add(passLabel, gbc); 
gbc.gridx = 1; 
passwordField = new JPasswordField(20); 
passwordField.setFont(fieldFont); 
mainPanel.add(passwordField, gbc); 
// Buttons 
JPanel buttonPanel = new JPanel(); 
buttonPanel.setBackground(new Color(222, 233, 250)); 
loginButton = new JButton("Login"); 
cancelButton = new JButton("Cancel"); 
signupButton = new JButton("Signup"); 
Font buttonFont = new Font("Arial", Font.BOLD, 16); 
loginButton.setFont(buttonFont); 
cancelButton.setFont(buttonFont); 
signupButton.setFont(buttonFont); 
buttonPanel.add(loginButton); 
buttonPanel.add(cancelButton); 
buttonPanel.add(signupButton); 
// Action Listeners 
loginButton.addActionListener(new LoginAction()); 
cancelButton.addActionListener(new ActionListener() { 
public void actionPerformed(ActionEvent e) { 
System.exit(0); 
} 
}); 
signupButton.addActionListener(new ActionListener() { 
public void actionPerformed(ActionEvent e) { 
dispose(); 
RegisterPage registerPage = new RegisterPage(); 
registerPage.setVisible(true); 
} 
}); 
// Button panel placement 
gbc.gridx = 0; 
gbc.gridy = 3; 
gbc.gridwidth = 2; 
gbc.anchor = GridBagConstraints.CENTER; 
mainPanel.add(buttonPanel, gbc); 
add(mainPanel); 
} 
private class LoginAction implements ActionListener { 
public void actionPerformed(ActionEvent e) { 
String username = usernameField.getText().trim(); 
String password = new String(passwordField.getPassword()).trim(); 
if (username.isEmpty() || password.isEmpty()) { 
JOptionPane.showMessageDialog(LoginPage.this, "Please fill in all fields.", "Warning", JOptionPane.WARNING_MESSAGE); 
return; 
} 
int userId = authenticate(username, password); 
if (userId != -1) { 
Session.setCurrentUser(userId, username); 
JOptionPane.showMessageDialog(LoginPage.this, "Login Successful"); 
new TopicSelectionPage(username).setVisible(true); 
dispose(); 
} else { 
JOptionPane.showMessageDialog(LoginPage.this, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE); 
} 
} 
} 
private int authenticate(String username, String password) { 
try { 
Connection conn = DBConnection.getConnection(); 
PreparedStatement stmt = conn.prepareStatement("SELECT id, password FROM users WHERE username = ?"); 
stmt.setString(1, username); 
ResultSet rs = stmt.executeQuery(); 
if (rs.next()) { 
String storedPassword = rs.getString("password"); 
if (storedPassword.equals(password)) { 
return rs.getInt("id"); 
} 
} 
rs.close(); 
stmt.close(); 
conn.close(); 
} catch (SQLException ex) { 
ex.printStackTrace(); 
} 
return -1; 
} 
public static void main(String[] args) { 
SwingUtilities.invokeLater(new Runnable() { 
public void run() { 
new LoginPage().setVisible(true); 
} 
}); 
} 
}