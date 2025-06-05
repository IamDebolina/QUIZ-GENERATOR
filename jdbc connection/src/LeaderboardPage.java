import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.util.ArrayList;

public class LeaderboardPage extends JDialog {

    // Constructor with optional parameters for saving score
    public LeaderboardPage(JFrame parent, String topicToUpdate, int newScore) {
        super(parent, "Leaderboard", true);
        setSize(800, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Save score if topic and score are provided
        String currentUsername = Session.getCurrentUsername();
        if (currentUsername != null && topicToUpdate != null && !topicToUpdate.isEmpty()) {
            saveScoreToDatabase(currentUsername, topicToUpdate, newScore);
        }

        // Build Leaderboard UI
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Leaderboard by Topic",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 16), Color.BLUE));

        Map<String, Map<String, Integer[]>> userScores = getUserScores();
        Set<String> topics = getTopics();

        // Modify the columns to be "Username", "Previous Marks", "Latest Marks"
        List<String> columnsList = new ArrayList<>();
        columnsList.add("Username");
        for (String topic : topics) {
            columnsList.add(topic + " (Previous Marks)");
            columnsList.add(topic + " (Latest Marks)");
        }

        String[] columns = columnsList.toArray(new String[0]);
        String[][] data = new String[userScores.size()][columns.length];

        int row = 0;
        for (Map.Entry<String, Map<String, Integer[]>> entry : userScores.entrySet()) {
            data[row][0] = entry.getKey();
            int col = 1;
            for (String topic : topics) {
                Integer[] scores = entry.getValue().getOrDefault(topic, new Integer[]{null, null});
                data[row][col++] = scores[0] != null ? scores[0].toString() : "-";
                data[row][col++] = scores[1] != null ? scores[1].toString() : "-";
            }
            row++;
        }

        JTable table = new JTable(data, columns);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Center the text in the columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setBackground(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Close Button with ActionListener using an anonymous inner class
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the dialog
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Save or update score in database
    private void saveScoreToDatabase(String username, String topic, int latestScore) {
        try (Connection conn = DBConnection.getConnection()) {
            if (!doesUserExist(username, conn)) {
                throw new SQLException("Username does not exist in the users table.");
            }

            String selectQuery = "SELECT latest_score FROM leaderboard WHERE username = ? AND topic = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                selectStmt.setString(1, username);
                selectStmt.setString(2, topic);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int previousScore = rs.getInt("latest_score");
                    String updateQuery = "UPDATE leaderboard SET previous_score = ?, latest_score = ? WHERE username = ? AND topic = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, previousScore);
                        updateStmt.setInt(2, latestScore);
                        updateStmt.setString(3, username);
                        updateStmt.setString(4, topic);
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertQuery = "INSERT INTO leaderboard (username, topic, previous_score, latest_score) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, username);
                        insertStmt.setString(2, topic);
                        insertStmt.setInt(3, 0); // previous = 0 for new entry
                        insertStmt.setInt(4, latestScore);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Get all distinct topics
    private Set<String> getTopics() {
        Set<String> topics = new TreeSet<>();
        String query = "SELECT DISTINCT topic FROM leaderboard";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                topics.add(rs.getString("topic"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topics;
    }

    // Get scores: username -> topic -> [previous, latest]
    private Map<String, Map<String, Integer[]>> getUserScores() {
        Map<String, Map<String, Integer[]>> userScores = new TreeMap<>();
        String query = "SELECT username, topic, previous_score, latest_score FROM leaderboard";

        boolean filterByUser = Session.getCurrentUsername() != null;
        if (filterByUser) query += " WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if (filterByUser) stmt.setString(1, Session.getCurrentUsername());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String username = rs.getString("username");
                    String topic = rs.getString("topic");
                    int prev = rs.getInt("previous_score");
                    int latest = rs.getInt("latest_score");

                    userScores.putIfAbsent(username, new HashMap<>());
                    userScores.get(username).put(topic, new Integer[]{prev, latest});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userScores;
    }

    // Check if user exists
    private boolean doesUserExist(String username, Connection conn) {
        String checkUserQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkUserQuery)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
