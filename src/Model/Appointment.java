package Model;

import static Model.AppointmentList.updateAppointmentList;
import static Model.DBManager.DB_URL;
import static Model.DBManager.pass;
import static Model.DBManager.user;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 *
 * @author kelsey hayden
 */
public class Appointment {
  
    private IntegerProperty appointmentId;
    private IntegerProperty customerId;
    private StringProperty title;
    private StringProperty description;
    private StringProperty location;
    private StringProperty contact;
    private StringProperty url;
    private Timestamp startTimestamp;
    private Timestamp endTimestamp;
    private Date startDate;
    private Date endDate;
    private StringProperty dateString;
    private StringProperty startString;
    private StringProperty endString;
    private StringProperty createdBy;
    
    //Constructor
    public Appointment(int appointmentId, int customerId, String title, String description, String location, String contact,
                       String url, Timestamp startTimestamp, Timestamp endTimestamp, Date startDate, Date endDate, String createdBy) {
        this.appointmentId = new SimpleIntegerProperty(appointmentId);
        this.customerId = new SimpleIntegerProperty(customerId);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
        this.location = new SimpleStringProperty(location);
        this.contact = new SimpleStringProperty(contact);
        this.url = new SimpleStringProperty(url);
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.startDate = startDate;
        this.endDate = endDate;
        SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
        this.dateString = new SimpleStringProperty(format.format(startDate));
        SimpleDateFormat formatTime = new SimpleDateFormat("hh:mm a z");
        this.startString = new SimpleStringProperty(formatTime.format(startDate));
        this.endString = new SimpleStringProperty(formatTime.format(endDate));
        this.createdBy = new SimpleStringProperty(createdBy);
    }

        //Setters
        public void setAppointmentId(int appointmentId) {
            this.appointmentId.set(appointmentId);
        }

        public void setCustomerId(int customerId) {
            this.customerId.set(customerId);
        }

        public void setTitle(String title) {
            this.title.set(title);
        }

        public void setDescription(String description) {
            this.description.set(description);
        }

        public void setLocation(String location) {
            this.location.set(location);
        }

        public void setContact(String contact) {
            this.contact.set(contact);
        }

        public void setUrl(String url) {
            this.url.set(url);
        }

        public void setStartTimestamp(Timestamp startTimestamp) {
            this.startTimestamp = startTimestamp;
        }

        public void setEndTimestamp(Timestamp endTimestamp) {
            this.endTimestamp = endTimestamp;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

        public void setCreatedBy (String createdBy) {
            this.createdBy.set(createdBy);
        }

        //Getters
        public int getAppointmentId() {
            return this.appointmentId.get();
        }

        public int getCustomerId() {
            return this.customerId.get();
        }

        public String getTitle() {
            return this.title.get();
        }

        public String getDescription() {
            return this.description.get();
        }

        public String getLocation() {
            return this.location.get();
        }

        public String getContact() {
            return this.contact.get();
        }

        public String getUrl() {
            return this.url.get();
        }

        public Timestamp getStartTimestamp() {
            return this.startTimestamp;
        }

        public Timestamp getEndTimestamp() {
            return this.endTimestamp;
        }

        public Date getStartDate() {
            return this.startDate;
        }

        public Date getEndDate() {
            return this.endDate;
        }

        public String getDateString() {
            return this.dateString.get();
        }

        public String getStartString() {
            return this.startString.get();
        }

        public String getEndString() {
            return this.endString.get();
        }

        public String getCreatedBy() {
            return this.createdBy.get();
        } 

        //Properties
        public StringProperty titleProperty() {
            return this.title;
        }

        public StringProperty descriptionProperty() {
            return this.description;
        }

        public IntegerProperty appointmentIdProperty() {
            return this.appointmentId;
        }

        public IntegerProperty customerIdProperty() {
            return this.customerId;
        }

        public StringProperty locationProperty() {
            return this.location;
        }

        public StringProperty contactProperty() {
            return this.contact;
        }

        public StringProperty urlProperty() {
            return this.url;
        }

        public StringProperty dateStringProperty() {
            return this.dateString;
        }

        public StringProperty startStringProperty() {
            return this.startString;
        }

        public StringProperty endStringProperty() {
            return this.endString;
        }

         public StringProperty createdByProperty() {
            return this.createdBy;
        }
     
         // Validating appointment 
        public static String isAppointmentValid(Customer customer, String title, String description, String location,
                                                LocalDate appointmentDate, String startHour, String startMinute, String startAmPm,
                                                String endHour, String endMinute, String endAmPm) throws NumberFormatException {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/Appointment", Locale.getDefault());
            String errorMessage = "";
            try {
                if (customer == null) {
                    errorMessage = errorMessage + rb.getString("errorCustomer");
                }
                if (title.length() == 0) {
                    errorMessage = errorMessage + rb.getString("errorTitle");
                }
                if (description.length() == 0) {
                    errorMessage = errorMessage + rb.getString("errorDescription");
                }
                if (location.length() == 0) {
                    errorMessage = errorMessage + rb.getString("errorLocation");
                }
                if (appointmentDate == null || startHour.equals("") || startMinute.equals("") || startAmPm.equals("") ||
                        endHour.equals("") || endMinute.equals("") || endAmPm.equals("")) {
                    errorMessage = errorMessage + rb.getString("errorStartEndIncomplete");
                }
                if (Integer.parseInt(startHour) < 1 || Integer.parseInt(startHour) > 12 || Integer.parseInt(endHour) < 1 || Integer.parseInt(endHour) > 12 ||
                        Integer.parseInt(startMinute) < 0 || Integer.parseInt(startMinute) > 59 || Integer.parseInt(endMinute) < 0 || Integer.parseInt(endMinute) > 59) {
                    errorMessage = errorMessage + rb.getString("errorStartEndInvalidTime");
                }
                if ((startAmPm.equals("PM") && endAmPm.equals("AM")) || (startAmPm.equals(endAmPm) && Integer.parseInt(startHour) != 12 && Integer.parseInt(startHour) > Integer.parseInt(endHour)) ||
                        (startAmPm.equals(endAmPm) && startHour.equals(endHour) && Integer.parseInt(startMinute) > Integer.parseInt(endMinute))) {
                    errorMessage = errorMessage + rb.getString("errorStartAfterEnd");
                }
                if ((Integer.parseInt(startHour) < 9 && startAmPm.equals("AM")) || (Integer.parseInt(endHour) < 9 && endAmPm.equals("AM")) ||
                        (Integer.parseInt(startHour) >= 9 && Integer.parseInt(startHour) < 12 && startAmPm.equals("PM")) || (Integer.parseInt(endHour) >= 9 && Integer.parseInt(endHour) < 12 && endAmPm.equals("PM")) ||
                        (Integer.parseInt(startHour) == 12 && startAmPm.equals("AM")) || (Integer.parseInt(endHour)) == 12 && endAmPm.equals("AM")) {
                    errorMessage = errorMessage + rb.getString("startEndOutsideHours");
                }
                if (appointmentDate.getDayOfWeek().toString().toUpperCase().equals("SATURDAY") || appointmentDate.getDayOfWeek().toString().toUpperCase().equals("SUNDAY")) {
                    errorMessage = errorMessage + rb.getString("errorDateIsWeekend");
                }
            }
            catch (NumberFormatException e) {
                errorMessage = errorMessage + rb.getString("errorStartEndInteger");
            }
            finally {
                return errorMessage;
            }
        }
        
         // Checks if new appointment overlaps with any existing appointments and return true if it does
            public static boolean appointmentOverlap(Timestamp startTimestamp, Timestamp endTimestamp) {
                updateAppointmentList();
                ObservableList<Appointment> appointmentList = AppointmentList.getAppointmentList();
                for (Appointment appointment: appointmentList) {
                    Timestamp existingStartTimestamp = appointment.getStartTimestamp();
                    Timestamp existingEndTimestamp = appointment.getEndTimestamp();
                    // Check various scenarios for where overlap would occur and return true if any occur
                    if (startTimestamp.after(existingStartTimestamp) && startTimestamp.before(existingEndTimestamp)) {
                        return true;
                    }
                    if (endTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                        return true;
                    }
                    if (startTimestamp.after(existingStartTimestamp) && endTimestamp.before(existingEndTimestamp)) {
                        return true;
                    }
                    if (startTimestamp.before(existingStartTimestamp) && endTimestamp.after(existingEndTimestamp)) {
                        return true;
                    }
                    if (startTimestamp.equals(existingStartTimestamp)) {
                        return true;
                    }
                    if (endTimestamp.equals(existingEndTimestamp)) {
                        return true;
                    }
                }
                // If none of the above scenarios occur, return false
                return false;
            }
        
       //Populates appointments for each customer by month and displays in top table.
    public static ObservableList<Appointment> getAppointmentsForMonth (int id) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Appointment appointment;
        LocalDate begin = LocalDate.now();
        LocalDate end = LocalDate.now().plusMonths(1);
        try(Connection conn =DriverManager.getConnection(DB_URL, user, pass);
                Statement statement = conn.createStatement()) {
           ResultSet resultSet = statement.executeQuery("SELECT * FROM appointment WHERE customerId = '" + id + "' AND " + 
                "start >= '" + begin + "' AND start <= '" + end + "'" );
           
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
    
    //Populates appointments for each customer by week and displays in top table on second tab.
     public static ObservableList<Appointment> getAppoinmentsForWeek(int id) {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();
        Appointment appointment;
        LocalDate beginWeek = LocalDate.now();
        LocalDate endWeek = LocalDate.now().plusWeeks(1);
        try (Connection conn =DriverManager.getConnection(DB_URL, user, pass);
                Statement statement = conn.createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM appointment WHERE customerId = '" + id + "' AND " + 
                "start >= '" + beginWeek + "' AND start <= '" + endWeek + "'");
       
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
          
      // Allert for appointment in 15 minutes
    public static Appointment appointmentIn15Min() {
        Appointment appointment;
        LocalDateTime now = LocalDateTime.now();
        ZoneId zid = ZoneId.systemDefault();
        ZonedDateTime zdt = now.atZone(zid);
        LocalDateTime ldt = zdt.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();
        LocalDateTime ldt2 = ldt.plusMinutes(15);
        String username = UserDB.getCurrentUser().getUserName();
       try (Connection conn =DriverManager.getConnection(DB_URL, user, pass);
                            Statement statement = conn.createStatement()) {
            String query = "SELECT * FROM appointment WHERE start BETWEEN '" + ldt + "' AND '" + ldt2 + "' AND " + 
                "createdBy='" + username + "'";
            ResultSet results = statement.executeQuery(query);
            if(results.next()) {
                appointment = new Appointment(results.getInt("appointmentId"), results.getInt("customerId"), results.getString("title"),
                    results.getString("end"), results.getString("contact"), results.getString("description"),results.getString("location"), 
                    results.getTimestamp("start"), results.getTimestamp("end"), results.getDate("createDate"), results.getDate("lastupDate"), results.getString("createdBy"));
                return appointment;
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        return null;
    }
 
}
