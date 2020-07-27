package ViewController;

import Model.Customer;
import Model.Appointment;
import static Model.Appointment.appointmentOverlap;
import Model.AppointmentList;
import  Model.CustomerList;
import  static Model.AppointmentList.updateAppointmentList;
import static Model.DBManager.pass;
import static Model.DBManager.user;
import static Model.DBManager.DB_URL;
import Model.UserDB;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kelsey hayden
 */
public class AddAppointmentScreenController implements Initializable {


            @FXML
            private Label addTitle;
            @FXML
            private Label titleLabel;
            @FXML
            private Label descriptionLabel;
            @FXML
            private Label locationLabel;
            @FXML
            private Label contactLabel;
            @FXML
            private Label urlLabel;
            @FXML
            private Label dateLabel;
            @FXML
            private Label startTimeLabel;
            @FXML
            private Label endTimeLabel;
            @FXML
            private Label typeLabel;
            @FXML
            private TextArea descriptionTextField;
            @FXML
            private TextField titleTextField;
            @FXML
            private TextField locationTextField;
            @FXML
            private TextField contactTextField;
            @FXML
            private TextField urlTextField;
            @FXML
            private TextField startTimeHourTextField;
            @FXML
            private TextField startTimeMinuteTextField;
            @FXML
            private TextField endTimeHourTextField;
            @FXML
            private TextField endTimeMinutetextField;
            @FXML
            private ChoiceBox<String> startAMPM;
            @FXML
            private ChoiceBox<String> endAMPM;
            @FXML
            private String AM;
            @FXML
            private String PM;
            @FXML
            private DatePicker datePicker;
            @FXML 
            private ComboBox typeCombo;

            //top table/add table
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

            //bottom table/ delete table
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
            private Button addButton;
            @FXML
            private Button deleteButton;
            @FXML
            private Button cancelButton;
            @FXML
            private Button saveButton;

            // ObservableList to hold customers currently that are currently assigned to the appointment
            private ObservableList<Customer> assignedCustomers = FXCollections.observableArrayList();
           

            @Override public void initialize(URL url, ResourceBundle rb) {
                    // TODO
                    setLanguage();
                    
                    //sets types list to type combo box
                    typeCombo.setItems(AppointmentList.getTypes());
                    
                    // Lambda Expressions for addTableView (top)     
                    addNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
                    addCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
                    addCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
                    addPhoneColumn.setCellValueFactory(cellData -> cellData.getValue().phoneProperty());

                    // Lambda Expressions for deleteTableView (bottom)  
                    deleteNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());
                    deleteCityColumn.setCellValueFactory(cellData -> cellData.getValue().cityProperty());
                    deleteCountryColumn.setCellValueFactory(cellData -> cellData.getValue().countryProperty());
                    deletePhoneColumn.setCellValueFactory(cellData -> cellData.getValue(). phoneProperty()); 

                    //intitializes table view methods
                    updateAddTableView();
                    updateDeleteTableView();
            }    
    
            //adding customer to the bottom table and assigning to the appoinment
          @FXML void addButtonHandler(ActionEvent event) throws IOException {
                   // Get selected customer from upper table view
                    Customer customer = addTableView.getSelectionModel().getSelectedItem();
                    ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
                    // Check if customer was selected
                    if (customer == null) {
                        // Create alert saying a customer must be selected 
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(rb.getString("error"));
                        alert.setHeaderText(rb.getString("error"));
                        alert.setContentText(rb.getString("errorSelect"));
                        alert.showAndWait();
                        return;
                    }
                    // Check if assignedCustomers list already contains a customer
                    if (assignedCustomers.size() > 0) {
                        // Create alert saying only one customer can be added to an appointment
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(rb.getString("error"));
                        alert.setHeaderText(rb.getString("error"));
                        alert.setContentText(rb.getString("errorOnlyOne"));
                        alert.showAndWait();
                        return;
                    }
                    // If no customers currently in assignedCustomer list, add selected customer
                    assignedCustomers.add(customer);
                    // Update lower table view to show newly added customer
                    updateDeleteTableView();
          }
          
          //deleting customer from the bottom table and removing from the appoinment
            @FXML void deleteButtonHandler(ActionEvent event) {
                    // Get selected customer from lower table view
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
                    // If 'OK' is selected remove customer from assignedCustomers list
                    if (result.get() == ButtonType.OK) {
                        assignedCustomers.remove(customer);
                        // Update lower table view to reflect customer being removed from appointment
                        updateDeleteTableView();
                    }
            }
          
            // Handles saving appointment. Add to database and checks for any overlaping appts.
            @FXML void saveButtonHandler(ActionEvent event) throws SQLException{
                           
                   // Initialize customer
                    Customer customer = null;
                    // Check if assignedCustomers list contains  a customer and get customer if it does
                    if (assignedCustomers.size() == 1) {
                        customer = assignedCustomers.get(0);
                    }
                    // Get other appointment information
                    String title = titleTextField.getText();
                    String description = descriptionTextField.getText();
                    String location = locationTextField.getText();
                    String contact = contactTextField.getText();
                    // If contact field has been left empty, fill with customers name and phone
                    if (contact.length() == 0 && customer != null) {
                        contact = customer.getCustomerName() + ", " + customer.getPhone();
                    }
                    String url = urlTextField.getText();
                    LocalDate appointmentDate = datePicker.getValue();
                    String startHour = startTimeHourTextField.getText();
                    String startMinute = startTimeMinuteTextField.getText();
                    String startAmPm = startAMPM.getSelectionModel().getSelectedItem();
                    String endHour = endTimeHourTextField.getText();
                    String endMinute = endTimeHourTextField.getText();
                    String endAmPm = endAMPM.getSelectionModel().getSelectedItem();
                    String type = typeCombo.getSelectionModel().getSelectedItem().toString();
                   
                    
                    // Submit appointment information for validation
                    String errorMessage = Appointment.isAppointmentValid(customer, title, description, location,
                            appointmentDate, startHour, startMinute, startAmPm, endHour, endMinute, endAmPm);
                    // Check if errorMessage contains anything
                    if (errorMessage.length() > 0) {
                        ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
                        // Show alert with errorMessage
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(rb.getString("error"));
                        alert.setHeaderText(rb.getString("apptInvalid"));
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
                        startLocal = localDateFormat.parse(appointmentDate.toString() + " " + startHour + ":" + startMinute + " " + startAmPm);
                        endLocal = localDateFormat.parse(appointmentDate.toString() + " " + endHour + ":" + endMinute + " " + endAmPm);
                    }
                    catch (ParseException e) {
                        e.printStackTrace();
                    }
                    // Create ZonedDateTime out of Date objects
                    ZonedDateTime startUTC = ZonedDateTime.ofInstant(startLocal.toInstant(), ZoneId.of("UTC"));
                    ZonedDateTime endUTC = ZonedDateTime.ofInstant(endLocal.toInstant(), ZoneId.of("UTC"));
                    // Submit information to be added to database. Check if 'true' is returned
                    if (addAppointment(customer, title, description, location,contact, url, startUTC, endUTC ,type)) {
                        try {
                            // Return to main screen
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
            }
            
            // Canceling out of the add appoinment screen
           @FXML private void cancelButtonHandler(ActionEvent event) throws IOException {
                    ResourceBundle resourceBundle = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
                    //Cancelling out of the add appointment screen
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.initModality(Modality.NONE);
                    alert.setTitle(resourceBundle.getString("confirmCancel"));
                    alert.setHeaderText(resourceBundle.getString("confirmCancel"));
                    alert.setContentText(resourceBundle.getString("confirmCancelAddMessage"));
                    Optional<ButtonType> result = alert.showAndWait();
                    // If the 'OK' button is clicked, return to main screen
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

             // Add appointment to database if entry does not already exist and checks for any overlap
            public static boolean addAppointment(Customer customer, String title, String description, String location, 
                                                 String contact, String url, ZonedDateTime startUTC, ZonedDateTime endUTC,String type ) throws SQLException  {
 
                // Transform ZonedDateTimes to Timestamps
                String startUTCString = startUTC.toString();
                startUTCString = startUTCString.substring(0,10) + " " + startUTCString.substring(11,16) + ":00";
                Timestamp startTimestamp = Timestamp.valueOf(startUTCString);
                String endUTCString = endUTC.toString();
                endUTCString = endUTCString.substring(0,10) + " " + endUTCString.substring(11,16) + ":00";
                Timestamp endTimestamp = Timestamp.valueOf(endUTCString);
                
                // Check if appointment overlaps with existing appointments. Show alert if it does.
                if (appointmentOverlap(startTimestamp, endTimestamp)) {
                    ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(rb.getString("error"));
                    alert.setHeaderText(rb.getString("errorAddingAppt"));
                    alert.setContentText(rb.getString("errorAppoinmentOverlap"));
                    alert.showAndWait();
                    return false;
                } else {
                    // Adds new appointment 
                    int customerId = customer.getCustomerId();
                     // Try-with-resources block for database connection
                    try (Connection conn = DriverManager.getConnection(DB_URL,user, pass);
                         Statement statement = conn.createStatement()) {
                        ResultSet results = statement.executeQuery("SELECT appointmentId FROM appointment ORDER BY appointmentId");
                        int appointmentId;
                        // Check last appointmentId value and add one to it for new appointmentId value
                        if (results.last()) {
                            appointmentId = results.getInt(1) + 1;
                            results.close();
                        }
                        else {
                            results.close();
                            appointmentId = 1;
                        }
                        //inserts entered info into database
                           String sqlString="INSERT INTO appointment(customerId,title,description, location, contact,url,start,end,createDate,createdBy,lastUpdate,lastUpdateBy, type)"
                                   + " VALUES ('" + customerId + "', '" + title + "', '" +description + "', '" + location +  "', '" + contact + "', '" + url + "', '" + startTimestamp
                                   + "', '" + endTimestamp + "', " +"CURRENT_DATE, '" + UserDB.getCurrentUser().getUserName() + "', CURRENT_TIMESTAMP, '" + UserDB.getCurrentUser() .getUserName()+ "', '" + type +"')";
                           statement.executeUpdate(sqlString);
                    }
                    catch (SQLException e) {
                        // Create alert notifying user that a database connection is needed for this function
                        ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(rb.getString("error"));
                        alert.setHeaderText(rb.getString("errorAddingAppt"));
                        alert.setContentText(rb.getString("errorRequiresDatabase"));
                        alert.showAndWait();
                    }
                    return true;
                }
        }  
            
           
         
            // Update upper table view/ add table
            public void updateAddTableView() {
                    addTableView.setItems(CustomerList.getCustomerList());
           }

            // Update lower table view/ delete table
            public void updateDeleteTableView() {
                    deleteTableView.setItems(assignedCustomers);
            }  
            
            //changing language based on computer locale default
            @FXML private void setLanguage(){
                    ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyAppt", Locale.getDefault());
                    addTitle.setText(rb.getString("addTitle"));
                    titleLabel.setText(rb.getString("title"));
                    
                    titleTextField.setPromptText(rb.getString("titlePrompt"));
                    descriptionLabel.setText(rb.getString("description"));
                    descriptionTextField.setPromptText(rb.getString("descriptionPrompt"));
                    locationLabel.setText(rb.getString("location"));
                    locationTextField.setPromptText(rb.getString("locationPrompt"));
                    contactLabel.setText(rb.getString("contact"));
                    contactTextField.setPromptText(rb.getString("contact"));
                    urlLabel.setText(rb.getString("url"));
                    urlTextField.setPromptText(rb.getString("url"));
                    dateLabel.setText(rb.getString("date"));
                    startTimeLabel.setText(rb.getString("startTime"));
                    endTimeLabel.setText(rb.getString("endTime"));
                    typeLabel.setText(rb.getString("type"));
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
