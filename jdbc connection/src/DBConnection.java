import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/quizdb?allowPublicKeyRetrieval=true&useSSL=false";
        String user = "root";
        String password = "debOLINAs11@";
        return DriverManager.getConnection(url, user, password);
    }
}
