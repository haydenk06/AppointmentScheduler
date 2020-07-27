package Utils;

/**
 *
 * @author kelsey hayden
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.ZonedDateTime;


public class Logger {
     private static final String FILENAME = "log.txt";
    
    public Logger() {}
    
    public static void log (String userName, boolean success) {
        try (FileWriter fw = new FileWriter(FILENAME, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter pw = new PrintWriter(bw)) {
            pw.println(ZonedDateTime.now() + " " + userName + (success ? " Success" : " Failure"));
        } catch (IOException e) {
            System.out.println("Logger Error: " + e.getMessage());
        }
    }
}
