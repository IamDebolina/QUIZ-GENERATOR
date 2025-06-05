import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Set the current username (this would normally come from a login process)
        String currentUsername = "exampleUser";  // Replace with actual login value
        Session.setCurrentUser(1, currentUsername);  // Assuming user ID 1 for example

        // Simulate the quiz start with a sample topic (e.g., "Math")
        String sampleTopic = "Math";  // Replace with any topic you want to test

        // Create an instance of JFrame as the parent window
        JFrame parentFrame = new JFrame("Main Frame");  // This is your parent frame

        // Set the default close operation and size for the parent frame
        parentFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close the application when the parent frame is closed
        parentFrame.setSize(800, 600);  // Set the size of the parent frame (optional)
        parentFrame.setLocationRelativeTo(null);  // Centers the parent frame on the screen

        // Make the parent frame visible (optional, depending on your needs)
        parentFrame.setVisible(false);  // Initially hide the parent frame

        // Create an instance of QuizUI using the parent JFrame, current username, and sample topic
        QuizUI quizUI = new QuizUI(parentFrame, Session.getCurrentUsername(), sampleTopic);

        // Show the quiz UI
        quizUI.setVisible(true);  // This will display the QuizUI frame
    }
}
