import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class TopicSelectionPage extends JFrame {
    private JComboBox<String> topicComboBox;
    private JButton startQuizButton;
    private JButton leaderboardButton;
    private String username;

    public TopicSelectionPage(String username) {
        this.username = username;

        setTitle("Select Quiz Topic");
        setSize(600, 400);  // Enlarged window size
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Select a Topic");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topicComboBox = new JComboBox<>();
        topicComboBox.setMaximumSize(new Dimension(250, 30));
        loadTopics();

        startQuizButton = new JButton("Start Quiz");
        startQuizButton.setFont(new Font("Arial", Font.PLAIN, 16));
        startQuizButton.setFocusPainted(false);
        startQuizButton.setBackground(new Color(59, 89, 182));
        startQuizButton.setForeground(Color.WHITE);
        startQuizButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startQuizButton.addActionListener(new StartQuizAction());

        leaderboardButton = new JButton("View Leaderboard");
        leaderboardButton.setFont(new Font("Arial", Font.PLAIN, 16));
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setBackground(new Color(59, 89, 182));
        leaderboardButton.setForeground(Color.WHITE);
        leaderboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaderboardButton.addActionListener(new ViewLeaderboardAction());

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(topicComboBox);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(startQuizButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(leaderboardButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void loadTopics() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT topic FROM questions")) {

            boolean hasTopics = false;
            while (rs.next()) {
                topicComboBox.addItem(rs.getString("topic"));
                hasTopics = true;
            }

            if (!hasTopics) {
                JOptionPane.showMessageDialog(this, "No topics available.", "Info", JOptionPane.INFORMATION_MESSAGE);
                startQuizButton.setEnabled(false);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading topics: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private class StartQuizAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String selectedTopic = (String) topicComboBox.getSelectedItem();
            if (selectedTopic != null && !selectedTopic.isEmpty()) {
                // Start the quiz by opening QuizUI and passing the current frame as the parent
                new QuizUI(TopicSelectionPage.this, username, selectedTopic).setVisible(true);
                dispose();  // Close the TopicSelectionPage window after quiz starts
            } else {
                JOptionPane.showMessageDialog(TopicSelectionPage.this, "Please select a topic first.");
            }
        }
    }

    private class ViewLeaderboardAction implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                // Open LeaderboardPage
                new LeaderboardPage(TopicSelectionPage.this, null, 0).setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TopicSelectionPage("exampleUser").setVisible(true));  // Replace with login user
    }
}
