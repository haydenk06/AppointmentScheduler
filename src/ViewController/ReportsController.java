package ViewController;

import static Model.DBManager.DB_URL;
import static Model.DBManager.pass;
import static Model.DBManager.user;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Locale;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 *
 * @author kelsey hayden
 */

public class ReportsController {
    @FXML
    private Label reportsLabel;
     @FXML
    private TextArea reports;
    @FXML
    private TextArea schedule;
    @FXML
    private TextArea appointments;
    @FXML
    private Tab apptByMonthTab;
    @FXML
    private Tab consultantScheduleTab;
    @FXML
    private Tab customerApptTab;
    @FXML
    private Button backButton;
    
    // Initialize screen elements
    public void initialize() {
        // Set local language
        setLanguage();
        //reports
        reportsHandle();
        scheduleHandle();
        appointmentsHandle();
    }
    
    //Cancels out of  Report screen and displays main screen
    @FXML public void backButtonHandler(ActionEvent event) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Resources/Reports", Locale.getDefault());
            try {
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
    }

    //Gets appointments and displays according to Month
    @FXML public void reportsHandle() {
        StringBuilder reportsText = new StringBuilder();
        try {
           try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
                Statement statement = conn.createStatement()) {
                String query = "SELECT description, MONTHNAME(start) as 'Month', COUNT(*) as 'Total' FROM appointment GROUP BY description, MONTH(start)";
                ResultSet results = statement.executeQuery(query);
               
                reportsText.append(String.format("%1$-30s %2$-60s %3$s \n", "Month", "Appointment Type", "Total"));
                reportsText.append(String.join("", Collections.nCopies(110, "-")));
                reportsText.append("\n");
                while(results.next()) {
                    reportsText.append(String.format("%1$-35s %2$-65s %3$d \n",
                            results.getString("Month"), results.getString("description"), results.getInt("Total")));
                }
            }
            reports.setText(reportsText.toString());
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }    
    
    // Gets appointment description,date and name and displays by consultant
   @FXML  public void scheduleHandle() {
        StringBuilder scheduleText = new StringBuilder();
        try {
          try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
                Statement statement = conn.createStatement()) {
                String query = "SELECT appointment.contact, appointment.description, customer.customerName, start, end " +
                        "FROM appointment JOIN customer ON customer.customerId = appointment.customerId " +
                        "GROUP BY appointment.contact, MONTH(start), start";
                ResultSet results = statement.executeQuery(query);
               
                scheduleText.append(String.format("%1$-25s %2$-70s %3$-30s %4$-45s %5$s \n",
                        "Consultant", "Appointment", "customerName", "Start", "End"));
                scheduleText.append(String.join("", Collections.nCopies(110, "-")));
                scheduleText.append("\n");
                while(results.next()) {
                    scheduleText.append(String.format("%1$-25s %2$-70s %3$-30s %4$-45s %5$s \n",
                            results.getString("contact"), results.getString("description"), results.getString("customerName"),
                            results.getString("start"), results.getString("end")));
                }
            }
            schedule.setText(scheduleText.toString());
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
    }
    
  //Gets the ammount of appointments  for each customer and displays in tab
   @FXML  public void appointmentsHandle() {
         StringBuilder  appointmentsText = new StringBuilder();
        try {
            try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
                Statement statement = conn.createStatement()) {
                String query = "SELECT customer.customerName, COUNT(*) as 'Total' FROM customer JOIN appointment " +
                        "ON customer.customerId = appointment.customerId GROUP BY customerName";
                ResultSet results = statement.executeQuery(query);
                appointmentsText.append(String.format("%1$-65s %2$-65s \n", "Customer", "Total Appointments"));
                appointmentsText.append(String.join("", Collections.nCopies(110, "-")));
                appointmentsText.append("\n");
                while(results.next()) {
                    appointmentsText.append(String.format("%1$s %2$65d \n",
                            results.getString("CustomerName"), results.getInt("Total")));
                }
            }
            appointments.setText(appointmentsText.toString());
        } catch (SQLException e) {
            System.out.println("SQLExcpetion: " + e.getMessage());
        }
    }

    // Set labels to local language (default is English)
    private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("Resources/Reports", Locale.getDefault());
        reportsLabel.setText(rb.getString("reportsLabel"));
        apptByMonthTab.setText(rb.getString("apptByMonthTab"));
        consultantScheduleTab.setText(rb.getString("consultantScheduleTab"));
        customerApptTab.setText(rb.getString("custcustomerApptTabomerSchedule"));
        backButton.setText(rb.getString("backButton"));
    }
}
