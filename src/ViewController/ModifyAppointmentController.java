package ViewController;

import Model.Appointment;
import static Model.Appointment.appointmentOverlap;
import Model.AppointmentList;
import static Model.AppointmentList.getAppointmentList;
import Model.Customer;
import static Model.CustomerList.getCustomerList;
import static Model.DBManager.DB_URL;
import static Model.DBManager.pass;
import static Model.DBManager.user;
import Model.UserDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * FXML Controller class
 *
 * @author kelsey hayden
 */

public class ModifyAppointmentController {
    @FXML
    private Label modifyTitle;
    @FXML
    private Label appointmentLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private TextField titleTextField;
    @FXML
    private Label descriptionLabel;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private Label locationLabel;
    @FXML
    private TextField locationTextField;
    @FXML
    private Label contactLabel;
    @ FXML 
    private TextField contactTextField;
    @FXML
    private Label urlLabel;
    @FXML
    private TextField urlTextField;
    @FXML
    private Label dateLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label startTimeLabel;
    @FXML
    private TextField startHour;
    @FXML
    private TextField startMinute;
    @FXML
    private ChoiceBox<String> startAMPM;
    @FXML
    private Label endTimeLabel;
    @FXML
    private TextField endHour;
    @FXML
    private TextField endMinute;
    @FXML
    private ChoiceBox<String> endAMPM;
    @FXML
    private Label typeLabel;
    @FXML
    private ComboBox typeCombo;
    @FXML
    private TableView<Customer> addTableView;
    @FXML
    private TableColumn<Customer, String> addNameColumn;
    @FXML
    private TableColumn<Customer, String> addCityColumn;
    @FXML
    private TableColumn<Customer, String> addCountryColumn;
    @FXML
    private TableColumn<Customer, String> addPhoneColumn;
    @FXML
    private Button addButton;
    @FXML
    private TableView<Customer> deleteTableView;
    @FXML
    private TableColumn<Customer, String> deleteNameColumn;
    @FXML
    private TableColumn<Customer, String> deleteCityColumn;
    @FXML
    private TableColumn<Customer, String> deleteCountryColumn;
    @FXML
    private TableColumn<Customer, String> deletePhoneColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    // Initialize appointment object
    private Appointment appointment;
    
    // Get index of appointment to be modified 
    int appointmentIndexToModify = AppointmentSummaryController.getAppointmentIndexToModify();
    
    // Initializes ObservableList to hold customers associated with the appointment
    private ObservableList<Customer> assignedCustomers = FXCollections.observableArrayList();
    
     // Initialize screen elements
        @FXML public void initialize() {
                // Set local language
                setLanguage();
                
                 //sets types list to type combo box
                  typeCombo.setItems(AppointmentList.getTypes());
                
                ///////loading appointment info into textfields//////
                //gets appointment to be modified by index value
                appointment = getAppointmentList().get(appointmentIndexToModify);

                // Getting appoinment info
                String title = appointment.getTitle();
                String description = appointment.getDescription();
                String location = appointment.getLocation();
                String contact = appointment.getContact();
                String url = appointment.getUrl();
                Date appointmentDate = appointment.getStartDate();
                
                // Transform appointmentDate into LocalDate
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                calendar.setTime(appointmentDate);
                int appointmentYear = calendar.get(Calendar.YEAR);
                int appointmentMonth = calendar.get(Calendar.MONTH) + 1;
                int appointmentDay = calendar.get(Calendar.DAY_OF_MONTH);
                LocalDate appointmentLocalDate = LocalDate.of(appointmentYear, appointmentMonth, appointmentDay);
                
                // Split time strings into hour, minute and AM/PM strings
                String sString = appointment.getStartString();
                String sHour = sString.substring(0,2);
                    if (Integer.parseInt(sHour) < 10) {
                        sHour = sHour.substring(1,2);
                    }
                String sMinute = sString.substring(3,5);
                String sAmPm = sString.substring(6,8);
                String eString = appointment.getEndString();
                String eHour = eString.substring(0,2);
                if (Integer.parseInt(eHour) < 10) {
                    eHour = eHour.substring(1,2);
                }
                String eMinute = eString.substring(3,5);
                String eAmPm = eString.substring(6,8);
                // Get customer to add to currentCustomers via customerId
                int customerId = appointment.getCustomerId();
                ObservableList<Customer> customerList = getCustomerList();
                for (Customer customer : customerList) {
                    if (customer.getCustomerId() == customerId) {
                         assignedCustomers.add(customer);
                     }
                 }
                //loads current appoinment info into fields on modify screen
                titleTextField.setText(title);
                descriptionTextArea.setText(description);
                locationTextField.setText(location);
                contactTextField.setText(contact);
                urlTextField.setText(url);
                datePicker.setValue(appointmentLocalDate);
                startHour.setText(sHour);
                startMinute.setText(sMinute);
                startAMPM.setValue(sAmPm);
                endHour.setText(eHour);
                endMinute.setText(eMinute);
                endAMPM.setValue(eAmPm);
                
                // Assigns data to table views
                addNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
                addCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
                addCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
                addPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
                deleteNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
                deleteCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
                deleteCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
                deletePhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());
                
                 // Update table views
                updateAddTableView();
                updateDeleteTableView();
        
    }

    // Add customer to the lower table view of current customers
    @FXML  private void addButtonHandler(ActionEvent event) {
        // Get selected customer from upper/add table view
        Customer customer = addTableView.getSelectionModel().getSelectedItem();
        ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
        // Check if customer was selected
        if (customer == null) {
            // Create alert saying a customer must be selected to be added
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddCustomer"));
            alert.setContentText(rb.getString("errorSelect"));
            alert.showAndWait();
            return;
        }
        // Check if assignedCustomers already contains a customer
        if (assignedCustomers.size() > 0) {
            // Create alert saying only one customer can be added to an appointment
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorAddCustomer"));
            alert.setContentText(rb.getString("errorOnlyOne"));
            alert.showAndWait();
            return;
        }
        // If no customers currently in currentCustomers, add selected customer
        assignedCustomers.add(customer);
        // Update lower table view to show newly added customer
        updateDeleteTableView();
    }

    // Remove customer from the lower table view and de-assigns from appointment
    @FXML private void deleteButtonHandler(ActionEvent event) {
        // Get selected customer from lower/ delete table view
        Customer customer = deleteTableView.getSelectionModel().getSelectedItem();
        ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
        // Check if customer was selected
        if (customer == null) {
            // Create alert saying a customer must be selected to be removed
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("error"));
            alert.setContentText(rb.getString("errorSelect"));
            alert.showAndWait();
            return;
        }
        // Show alert to confirm removing customer from appointment
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmRemoveCustomer"));
        alert.setHeaderText(rb.getString("confirmRemoveCustomer"));
        alert.setContentText(rb.getString("confirmRemoveCustomerMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // If 'OK' is selected remove customer from currentCustomers
        if (result.get() == ButtonType.OK) {
            assignedCustomers.remove(customer);
            // Update lower/delete table view to reflect customer being removed from appointment
            updateDeleteTableView();
        }
    }

    // Submit appointment information to be updated in database
    @FXML private void saveButtonHandler(ActionEvent event) {
        // Initialize customer
        Customer customer = null;
        // Check if currentCustomers contains a customer and get customer if it does
        if (assignedCustomers.size() == 1) {
            customer = assignedCustomers.get(0);
        }
        // Get other appointment information
        int appointmentId = appointment.getAppointmentId();
        String title = titleTextField.getText();
        String description = descriptionTextArea.getText();
        String location = locationTextField.getText();
        String contact = contactTextField.getText();
        // If contact field has been left empty, fill with customers name and phone
        if (contact.length() == 0 && customer != null) {
            contact = customer. getCustomerName() + ", " + customer.getPhone();
        }
        String url = urlTextField.getText();
        LocalDate appointmentDate = datePicker.getValue();
        String sHour = startHour.getText();
        String sMinute = startMinute.getText();
        String sAmPm = startAMPM.getSelectionModel().getSelectedItem();
        String eHour = endHour.getText();
        String eMinute = endMinute.getText();
        String eAmPm = endAMPM.getSelectionModel().getSelectedItem();
        String type = typeCombo.getSelectionModel().getSelectedItem().toString();
        // Submit appointment information for validation
        String errorMessage = Appointment.isAppointmentValid(customer, title, description, location,
                appointmentDate, sHour, sMinute, sAmPm, eHour, eMinute, eAmPm);
        // Check if errorMessage contains anything
        if (errorMessage.length() > 0) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
            // Show alert with errorMessage
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingAppt"));
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return;
        }
        SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        localDateFormat.setTimeZone(TimeZone.getDefault());
        Date startLocal = null;
        Date endLocal = null;
        // Format date and time strings into Date objects
        try {
            startLocal = localDateFormat.parse(appointmentDate.toString() + " " + sHour + ":" + sMinute + " " + sAmPm);
            endLocal = localDateFormat.parse(appointmentDate.toString() + " " + eHour + ":" + eMinute + " " + eAmPm);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        // Create ZonedDateTime out of Date objects
        ZonedDateTime startUTC = ZonedDateTime.ofInstant(startLocal.toInstant(), ZoneId.of("UTC"));
        ZonedDateTime endUTC = ZonedDateTime.ofInstant(endLocal.toInstant(), ZoneId.of("UTC"));
        // Submit information to be updated in database. Check if 'true' is returned
        if (modifyAppointment(appointmentId, customer, title, description, location, contact, url, startUTC, endUTC, type)) {
            try {
                // Return to appointment summary window
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("AppointmentSummary.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Cancels modifying appointment and displays main screen
    @FXML  private void cancelButtonHandler(ActionEvent event) {
        ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
        // Show alert to confirm cancel
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmCancel"));
        alert.setHeaderText(rb.getString("confirmCancel"));
        alert.setContentText(rb.getString("confirmCancelModifyMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // If 'OK' button was selected, return to appointment summary window
        if(result.get() == ButtonType.OK) {
            try {
                Parent mainScreenParent = FXMLLoader.load(getClass().getResource("AppointmentSummary.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Updates upper table view
    public void updateAddTableView() {
        addTableView.setItems(getCustomerList());
    }

    // Updates lower table view
    public void updateDeleteTableView() {
        deleteTableView.setItems(assignedCustomers);
    }

    // used to change information for appointments
    public static boolean modifyAppointment(int appointmentId, Customer customer, String title, String description, String location,
                                         String contact, String url, ZonedDateTime startUTC, ZonedDateTime endUTC, String type) {
        try{
            // converting ZonedDateTimes to Timestamps
            String startUTCString = startUTC.toString();
            startUTCString = startUTCString.substring(0, 10) + " " + startUTCString.substring(11, 16) + ":00";
            Timestamp startTimestamp = Timestamp.valueOf(startUTCString);
            String endUTCString = endUTC.toString();
            endUTCString = endUTCString.substring(0, 10) + " " + endUTCString.substring(11, 16) + ":00";
            Timestamp endTimestamp = Timestamp.valueOf(endUTCString);
            
            //checking for overlaps
            if (appointmentOverlap(startTimestamp, endTimestamp)) {
                ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("errorModifyingApt"));
                alert.setContentText(rb.getString("errorAptsOverlap"));
                alert.showAndWait();
                return false;
            } else {
                //modifies appointment
                int customerId = customer.getCustomerId();
                updateAppointment(appointmentId, customerId, title, description, location, contact, url, startTimestamp, endTimestamp, type);
                return true;
            }
        } catch (Exception e) {
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resourceBundle.getString("error"));
            alert.setHeaderText(resourceBundle.getString("errorModifyingAppt"));
            alert.setContentText(resourceBundle.getString("errorRequiresDatabase"));
            alert.showAndWait();
            return false;
        }
    }
  
      // checks if appointment times are valid and updates appoint if true
    private static void updateAppointment(int appointmentId, int customerId, String title, String description, String location,
                                          String contact, String url, Timestamp startTimestamp, Timestamp endTimestamp, String type) throws SQLException {
        // Try-with-resources block for database connection
        try (Connection conn = DriverManager.getConnection(DB_URL,user, pass);
             Statement statement = conn.createStatement()) {
             statement.executeUpdate("UPDATE appointment SET customerId = " + customerId + ", title = '" + title + "', description = '" + description + "', " +
                    "location = '" + location + "', contact = '" + contact + "', url = '" + url + "', start = '" + startTimestamp + "', end = '" + endTimestamp + "', " +
                    "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + UserDB.getCurrentUser().getUserName() + "', type = '"+ type +"' WHERE appointmentId = " + appointmentId);
                     
        }
    }
    
       // Set labels to local language (default is English)
    @FXML private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
        modifyTitle.setText(rb.getString("modifyTitle"));
        titleLabel.setText(rb.getString("title")); 
        descriptionLabel.setText(rb.getString("description"));   
        locationLabel.setText(rb.getString("location"));    
        contactLabel.setText(rb.getString("contact"));
        urlLabel.setText(rb.getString("url"));
        datePicker.setAccessibleText(rb.getString("date"));
        startTimeLabel.setText(rb.getString("startTime"));
        endTimeLabel.setText(rb.getString("endTime"));
        typeLabel.setText(rb.getString("endTime"));
        addNameColumn.setText(rb.getString("name"));
        addCityColumn.setText(rb.getString("city"));
        addCountryColumn.setText(rb.getString("country"));
        addPhoneColumn.setText(rb.getString("phone"));
        deleteNameColumn.setText(rb.getString("name"));
        deleteCityColumn.setText(rb.getString("city"));
        deleteCountryColumn.setText(rb.getString("country"));
        deletePhoneColumn.setText(rb.getString("phone"));
        addButton.setText(rb.getString("add"));
        deleteButton.setText(rb.getString("delete"));
        saveButton.setText(rb.getString("save"));
        cancelButton.setText(rb.getString("cancel"));
    }
}
