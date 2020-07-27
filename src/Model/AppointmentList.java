package Model;

import static Model.DBManager.DB_URL;
import static Model.DBManager.pass;
import static Model.DBManager.user;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

/**
 *
 * @author kelsey hayden
 */
public class AppointmentList {
  
          private static ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();
          
          // list for type of appointments
           private static ObservableList<String> types = FXCollections.observableArrayList(
                "Request ","Next Available");
           public static ObservableList<String>getTypes(){
               return types;
           }
           
           //getting list of appointment types
           public static ObservableList<Appointment> getAppointmentList() {
                return appointmentList;
         }
           
            //getting list of appointments for the week view calendar
           public static ObservableList<Appointment> getAppointmentListWeek() {
               ObservableList<Appointment> appointments = FXCollections.observableArrayList();
                Appointment appointment;
                LocalDate beginWeek = LocalDate.now();
                LocalDate endWeek = LocalDate.now().plusWeeks(1);
                try (Connection conn =DriverManager.getConnection(DB_URL, user, pass);
                        Statement statement = conn.createStatement()) {
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM appointment WHERE  start >= '" + beginWeek + "' AND start <= '" + endWeek + "'");

                   while(resultSet.next()) {
                  appointment = new Appointment(resultSet.getInt("appointmentId"),resultSet.getInt("customerId"), resultSet.getString("title"),
                    resultSet.getString("description"),  resultSet.getString("location"),resultSet.getString("contact"),resultSet.getString("url"), 
                         resultSet.getTimestamp("start"), resultSet.getTimestamp("end"), resultSet.getDate("start"), 
                         resultSet.getDate("end"),resultSet.getString("createdby"));
                     appointments.add(appointment);
                    }

                    statement.close();
                    return appointments;
                } catch (SQLException e) {
                    System.out.println("SQLException: " + e.getMessage());
                    return null;
                }
         }
          
          // Updates appointmentList with all current appointments that have yet to occur
         public static void updateAppointmentList() {
                // Try-with-resources block for database connection
                try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
                     Statement stmt = conn.createStatement()) {
                    // Retrieve appointmentList and clear
                    ObservableList<Appointment> appointmentList = AppointmentList.getAppointmentList();
                    appointmentList.clear();
                    // Create list of appointmentId's for all appointments that are in the future
                    ResultSet appointmentResultSet = stmt.executeQuery("SELECT appointmentId FROM appointment WHERE start >= CURRENT_TIMESTAMP");
                    ArrayList<Integer> appointmentIdList = new ArrayList<>();
                    while(appointmentResultSet.next()) {
                        appointmentIdList.add(appointmentResultSet.getInt(1));
                    }
                    // Create Appointment object for each appointmentId in list and add Appointment to appointmentList
                    for (int appointmentId : appointmentIdList) {
                            // Retrieve appointment info from database
                            appointmentResultSet = stmt.executeQuery("SELECT customerId, title, description, location, contact, url, start, end, createdBy FROM appointment WHERE appointmentId = " + appointmentId);
                            appointmentResultSet.next();
                            int customerId = appointmentResultSet.getInt(1);
                            String title = appointmentResultSet.getString(2);
                            String description = appointmentResultSet.getString(3);
                            String location = appointmentResultSet.getString(4);
                            String contact = appointmentResultSet.getString(5);
                            String url = appointmentResultSet.getString(6);
                            Timestamp startTimestamp = appointmentResultSet.getTimestamp(7);
                            Timestamp endTimestamp = appointmentResultSet.getTimestamp(8);
                            String createdBy = appointmentResultSet.getString(9);
                            // Transform startTimestamp and endTimestamp to ZonedDateTimes
                            DateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
                            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            java.util.Date startDate = utcFormat.parse(startTimestamp.toString());
                            java.util.Date endDate = utcFormat.parse(endTimestamp.toString());
                            // Assign appointment info to new Appointment object
                            Appointment appointment = new Appointment(appointmentId, customerId, title, description, location, contact, url, startTimestamp, endTimestamp, startDate, endDate, createdBy);
                            // Add new Appointment object to appointmentList
                            appointmentList.add(appointment);
                    }
               } catch (Exception e) {
                    // Create alert notifying user that a database connection is needed for this function
                    ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(rb.getString("error"));
                    alert.setHeaderText(rb.getString("addingAppointment"));
                    alert.setContentText(rb.getString("errorRequiresDatabase"));
                    alert.showAndWait();
                }
         }

}
