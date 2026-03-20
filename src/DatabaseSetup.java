import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class DatabaseSetup {

    public static void setupDatabase() {
        createUsersTable();
        insertTestAccount();
    }

    private static void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL, "
                + "balance REAL DEFAULT 0"
                + ");";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("Users table ready.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertTestAccount() {
        String sql = "INSERT OR IGNORE INTO users(username, password, balance) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, "testuser");
            pst.setString(2, "1234");
            pst.setDouble(3, 1000.0);
            pst.executeUpdate();

            System.out.println("Test account ready.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}