package Model;

import static Model.DBManager.DB_URL;
import static Model.DBManager.pass;
import static Model.DBManager.user;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author kelsey hayden
 */

public class UserDB {
    
    
   private static User currentUser;
    
    public static User getCurrentUser() {
        return currentUser;
    }
    
    // Attempt Login
    public static Boolean login(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL,user, pass);
                         Statement statement = conn.createStatement()) {
            String query = "SELECT * FROM user WHERE userName='" + username + "' AND password='" + password + "'";
            ResultSet results = statement.executeQuery(query);
            if(results.next()) {
                currentUser = new User();
                currentUser.setUserName(results.getString("userName"));
                statement.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            return false;
        }
    }
}
