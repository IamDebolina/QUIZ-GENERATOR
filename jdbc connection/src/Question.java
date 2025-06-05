public class Question {
    int id;
    String question;
    String optionA, optionB, optionC, optionD;
    char correctOption;

    // Constructor to initialize the fields
    public Question(int id, String question, String a, String b, String c, String d, char correctOption) {
        this.id = id;
        this.question = question;
        this.optionA = a;
        this.optionB = b;
        this.optionC = c;
        this.optionD = d;
        this.correctOption = correctOption;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public char getCorrectOption() {
        return correctOption;
    }

    // Optional: A method to get the correct option as a String
    public String getCorrectOptionAsString() {
        switch (correctOption) {
            case 'A': return optionA;
            case 'B': return optionB;
            case 'C': return optionC;
            case 'D': return optionD;
            default: return null;
        }
    }
}
