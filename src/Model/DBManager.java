package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author kelsey hayden
 */

public class DBManager {
    private static  String databaseName = "U04W9u";
    public static  String DB_URL = "jdbc:mysql://52.206.157.109/" + databaseName;
    public static String user = "U04W9u";
    public static String pass = "53688361587";
    private static  String driver = "com.mysql.jdbc.Driver";
    public static Connection conn;
    
     //Connect to database
    public static void makeConnection() throws ClassNotFoundException, SQLException, Exception {
        Class.forName(driver);
        conn = (Connection) DriverManager.getConnection(DB_URL, user, pass);
        System.out.println("Connection Successful!");
    }
    
    //Disconnect from database
    public static void closeConnection() throws ClassNotFoundException, SQLException, Exception {
        conn.close();
        System.out.println("Connection Closed!");
    }
          
}
