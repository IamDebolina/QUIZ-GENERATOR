import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizUI extends JFrame {
    private JLabel questionLabel;
    private JRadioButton optionA, optionB, optionC, optionD;
    private ButtonGroup optionsGroup;
    private JButton nextButton;

    private List<Question> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;

    private final String username;
    private final String topic;
    private final JFrame parent; // Store parent frame

    public QuizUI(JFrame parent, String username, String topic) {
        this.parent = parent;
        this.username = username;
        this.topic = topic;

        setTitle("Quiz - " + topic);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        questionLabel = new JLabel("Question");
        questionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(questionLabel);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(4, 1));

        optionA = new JRadioButton();
        optionB = new JRadioButton();
        optionC = new JRadioButton();
        optionD = new JRadioButton();

        optionsGroup = new ButtonGroup();
        optionsGroup.add(optionA);
        optionsGroup.add(optionB);
        optionsGroup.add(optionC);
        optionsGroup.add(optionD);

        centerPanel.add(optionA);
        centerPanel.add(optionB);
        centerPanel.add(optionC);
        centerPanel.add(optionD);

        JPanel bottomPanel = new JPanel();
        nextButton = new JButton("Next");
        nextButton.addActionListener(new NextButtonAction());
        bottomPanel.add(nextButton);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadQuestions();
        showQuestion();
    }

    private void loadQuestions() {
        questions = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions WHERE topic = ?")) {
            stmt.setString(1, topic);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                questions.add(new Question(
                        rs.getInt("id"),
                        rs.getString("question"),
                        rs.getString("optionA"),
                        rs.getString("optionB"),
                        rs.getString("optionC"),
                        rs.getString("optionD"),
                        rs.getString("correctOption").charAt(0)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showQuestion() {
        if (currentQuestionIndex < questions.size()) {
            Question q = questions.get(currentQuestionIndex);
            questionLabel.setText("<html>Q" + (currentQuestionIndex + 1) + ": " + q.getQuestion() + "</html>");
            optionA.setText(q.getOptionA());
            optionB.setText(q.getOptionB());
            optionC.setText(q.getOptionC());
            optionD.setText(q.getOptionD());
            optionsGroup.clearSelection();
        }
    }

    private class NextButtonAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (!optionA.isSelected() && !optionB.isSelected() && !optionC.isSelected() && !optionD.isSelected()) {
                JOptionPane.showMessageDialog(QuizUI.this, "Please select an answer.");
                return;
            }

            String selected = null;
            if (optionA.isSelected()) selected = "A";
            else if (optionB.isSelected()) selected = "B";
            else if (optionC.isSelected()) selected = "C";
            else if (optionD.isSelected()) selected = "D";

            Question currentQ = questions.get(currentQuestionIndex);
            if (Character.toString(currentQ.getCorrectOption()).equalsIgnoreCase(selected)) {
                score++;
            }

            currentQuestionIndex++;
            if (currentQuestionIndex < questions.size()) {
                showQuestion();
            } else {
                saveScoreToDatabase();

                JOptionPane.showMessageDialog(QuizUI.this,
                        "Quiz completed!\nYour Score: " + score + "/" + questions.size(),
                        "Result", JOptionPane.INFORMATION_MESSAGE);

                int option = JOptionPane.showOptionDialog(
                        QuizUI.this,
                        "Would you like to go to the leaderboard or return to the topic selection?",
                        "Next Step",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new Object[]{"Leaderboard", "Return to Topic Selection"},
                        null
                );

                dispose();
                if (option == JOptionPane.YES_OPTION) {
                    new LeaderboardPage(parent, null, 0).setVisible(true);
                } else {
                    new TopicSelectionPage(username).setVisible(true);
                }
            }
        }
    }

    private void saveScoreToDatabase() {
        try (Connection conn = DBConnection.getConnection()) {
            String checkQuery = "SELECT * FROM leaderboard WHERE username = ? AND topic = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, topic);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int previousScore = rs.getInt("latest_score");
                    String updateQuery = "UPDATE leaderboard SET previous_score = ?, latest_score = ? WHERE username = ? AND topic = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, previousScore);
                        updateStmt.setInt(2, score);
                        updateStmt.setString(3, username);
                        updateStmt.setString(4, topic);
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertQuery = "INSERT INTO leaderboard (username, topic, previous_score, latest_score) VALUES (?, ?, 0, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, topic);
                        insertStmt.setInt(3, score);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
