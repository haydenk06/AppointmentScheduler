package ViewController;

import Model.Appointment;
import Model.AppointmentList;
import Model.Customer;
import Model.CustomerList;
import static Model.Appointment.getAppoinmentsForWeek;
import static Model.Appointment.getAppointmentsForMonth;
import static Model.AppointmentList.updateAppointmentList;
import static Model.CustomerList.updateCustomerList;
import static Model.CustomerList.getCustomerList;
import static Model.DBManager.DB_URL;
import static Model.DBManager.pass;
import static Model.DBManager.user;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
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
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * FXML Controller class
 *
 * @author kelsey hayden
 */

public class MainScreenController implements Initializable {
    @FXML
    private GridPane mainGrid;
   @FXML
    private Button getAppointmentsButton;
    @FXML
    private Button summaryButton;
    @FXML
    private Button reportsButton;
     @FXML
    private Button addButton;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button logButton;
    @FXML
    private Button exitButton;
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> addressColumn;
    @FXML
    private TableColumn<Customer, String> address2Column;
    @FXML
    private TableColumn<Customer, String> cityColumn;
    @FXML
    private TableColumn<Customer, String> countryColumn;
    @FXML
    private TableColumn<Customer, Integer> phoneColumn;
    @FXML
   TableView<Appointment> monthTableView;
     @FXML 
    private Tab monthViewTab;
    @FXML
    private TableColumn<Appointment, String>apptTitle;
    @FXML
    private TableColumn<Appointment, String>apptDescription;
    @FXML
    private TableColumn<Appointment, String>apptLocation;
    @FXML
    private TableColumn<Appointment, String>apptDate;
    @FXML
    private TableColumn<Appointment, String>apptContact;
    @FXML
   TableView<Appointment> weekTableView;
     @FXML 
    private Tab weekViewTab;
    @FXML
    private TableColumn<Appointment, String>apptTitleWeek;
    @FXML
    private TableColumn<Appointment, String>apptDescriptionWeek;
    @FXML
    private TableColumn<Appointment, String>apptLocationWeek;
    @FXML
    private TableColumn<Appointment, String>apptDateWeek;
    @FXML
    private TableColumn<Appointment, String>apptContactWeek;
   
    
      //select customer from customer table to view appointments
    private Customer selectedCustomer;
    
     // Holds index of the customer that will be modified
    private static int customerIndexToModify ;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setLanguage();
        
        //initializes alert for appointment in the next 15 mintues
        Appointment appointment = Appointment.appointmentIn15Min();
        if(appointment != null) {
            Customer customer = Customer.getCustomer(appointment.getCustomerId());
            String text = String.format("You have a %s appointment with %s at %s",
                appointment.getTitle(),
                customer.getCustomerName(),
                appointment.getStartTimestamp());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("reminder");
            alert.setHeaderText("reminderMessage");
            alert.setContentText(text);
            alert.showAndWait();
        }
                 
        //customer table
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        address2Column.setCellValueFactory(new PropertyValueFactory<>("address2"));
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        countryColumn.setCellValueFactory(new PropertyValueFactory<>("country"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        
        //load data for customer table
        customerTableView.setItems(CustomerList.getCustomerList()); 
        
         // Update the customer table view
        updateCustomerTableView();
        
        //Appointment calendar month table view
        apptTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        apptDescription.setCellValueFactory(new PropertyValueFactory<>("Description"));
        apptLocation.setCellValueFactory(new PropertyValueFactory<>("Location"));
        apptContact.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        apptDate.setCellValueFactory(new PropertyValueFactory<>("startTimestamp"));
        
          //Appointment calendar week table view
        apptTitleWeek.setCellValueFactory(new PropertyValueFactory<>("Title"));
        apptDescriptionWeek.setCellValueFactory(new PropertyValueFactory<>("Description"));
        apptLocationWeek.setCellValueFactory(new PropertyValueFactory<>("Location"));
        apptContactWeek.setCellValueFactory(new PropertyValueFactory<>("Contact"));
        apptDateWeek.setCellValueFactory(new PropertyValueFactory<>("startTimestamp"));
        
        
         //load data for calendar views
        monthTableView.setItems(AppointmentList.getAppointmentList()); 
        weekTableView.setItems(AppointmentList.getAppointmentListWeek()); 

        // Update appointment list
        updateAppointmentList();
      
    }    

    // Gets appointment information for each customer by Month and Week.
    @FXML public void getAppointmentsHandler(ActionEvent event) {
       ResourceBundle rb = ResourceBundle.getBundle("Resources/MainScreen", Locale.getDefault());
        selectedCustomer = customerTableView.getSelectionModel().getSelectedItem();
        if (selectedCustomer == null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("error"));
            alert.setContentText(rb.getString("errorGetAppt"));
        }else{
                    int id = selectedCustomer.getCustomerId();
                    monthTableView.setItems(getAppointmentsForMonth(id));
                    weekTableView.setItems(getAppoinmentsForWeek(id));
        }
    }

    //Opens appointment summary screen and populates info for each appointment. Allows modification of appointments.
     @FXML void summaryButtonHandler(ActionEvent event) throws IOException{
         //displaying summary screen for appointments
        Stage stage; 
        Parent root;       
        stage=(Stage) summaryButton.getScene().getWindow();
        FXMLLoader loader=new FXMLLoader(getClass().getResource(
               "AppointmentSummary.fxml"));
        root =loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    // Handles adding new customers
    @FXML void addButtonHandler (ActionEvent event) throws IOException {
        //displaying add customer screen
        Stage stage; 
        Parent root;       
        stage=(Stage) addButton.getScene().getWindow();
        FXMLLoader loader=new FXMLLoader(getClass().getResource(
               "AddCustomerScreen.fxml"));
        root =loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
     }

    //Handles modifing existing customers
    @FXML void modifyButtonHandler(ActionEvent event) throws IOException {
         ResourceBundle rb = ResourceBundle.getBundle("Resources/MainScreen", Locale.getDefault());
          Customer customerToModify = customerTableView.getSelectionModel().getSelectedItem();
        //checking if there is a customer selected
        if ( customerToModify == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorModifyingCustomer"));
            alert.setContentText(rb.getString("errorModifyingMessage"));
            Optional<ButtonType> result = alert.showAndWait();
        } else {
            //loading index info to modify customer screen
            customerIndexToModify = getCustomerList().indexOf(customerToModify);
            //displaying modify part screen
            Stage stage; 
            Parent root;       
            stage=(Stage) modifyButton.getScene().getWindow();
            FXMLLoader loader=new FXMLLoader(getClass().getResource( "ModifyCustomer.fxml"));
            root =loader.load();  
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
        
    }
    
    //Handles deleting Customer from table and list. Also sets customer to inactive in database.
    @FXML void deleteButtonHandler(ActionEvent event) throws SQLException{
         ResourceBundle rb = ResourceBundle.getBundle("Resources/MainScreen", Locale.getDefault());
         Customer customerToRemove = customerTableView.getSelectionModel().getSelectedItem();
        //deleting customer and updating list and table
        if (customerToRemove == null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("errorDelete"));
            alert.setContentText(rb.getString("errorDeleteMessage"));
            alert.showAndWait();
            return;
        }else{
            // Submit customer to be removed
            setInactive(customerToRemove);
              
           
        }
    }
    
    //Opens Log.txt file which shows user activity with timestamp information
    @FXML  public void logButtonHandler() {
        File file = new File("log.txt");
        if(file.exists()) {
            if(Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    System.out.println("Error Opening Log File: " + e.getMessage());
                }
            }
        }
    }
    
    //Opens report screens and populates data for each report in tab form.
    @FXML void reportsButtonHandler(ActionEvent event) throws IOException{
        //displaying reports screen
       try {
            Parent reportsParent = FXMLLoader.load(getClass().getResource("Reports.fxml"));
            Scene reportsScene = new Scene(reportsParent);
            Stage reportsStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            reportsStage.setScene(reportsScene);
            reportsStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
   
     //Handles exiting of application
    @FXML void exitButtonHandler(ActionEvent event) {
        ResourceBundle rb = ResourceBundle.getBundle("Resources/MainScreen", Locale.getDefault());
        //exiting out of main program
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirm"));
        alert.setHeaderText(rb.getString("exitConfirm"));
        alert.setContentText(rb.getString("exitMessage"));
        Optional<ButtonType> result = alert.showAndWait();
            if(result.get() == ButtonType.OK){
                System.exit(0);
            }else{
                alert.close();
            }
        }
    
    // Returns the customer to be modified
    public static int customerIndexToModify () {
        return customerIndexToModify  ;
    }
    
    // Updates the table customer view to display all customers
    public void updateCustomerTableView() {
        CustomerList.updateCustomerList();
        customerTableView.setItems(getCustomerList());
    }

    // Sets customer to "inactive" and hides them in the customer list
    public static void setInactive(Customer customerToRemove) {
        int customerId = customerToRemove.getCustomerId();
        ResourceBundle rb = ResourceBundle.getBundle("Resources/MainScreen", Locale.getDefault());
        // Show alert to confirm the removal of customer
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirm"));
        alert.setHeaderText(rb.getString("confirm"));
        alert.setContentText(rb.getString("confirmMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // Check if OK button was clicked and sets customer entry to inactive 
        if (result.get() == ButtonType.OK) {
            // Try-with-resources block for database connection
            try (Connection conn = DriverManager.getConnection(DB_URL,user, pass);
                 Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("UPDATE customer SET active = 0 WHERE customerId = " + customerId);
            }
            catch (SQLException e) {
                // Create alert notifying user that a database connection is needed for this function
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                alert2.setTitle(rb.getString("error"));
                alert2.setHeaderText(rb.getString("errorModifyingCustomer"));
                alert2.setContentText(rb.getString("errorRequiresDatabase"));
                alert2.showAndWait();
            }
            // Updates customerList, which will remove the inactive customer from the roster
            updateCustomerList();
        }
    }
     
    //changing language based on computer locale default
    private void setLanguage(){
        ResourceBundle rb = ResourceBundle.getBundle("Resources/MainScreen", Locale.getDefault());
        //buttons
        reportsButton.setText(rb.getString("reports"));
        getAppointmentsButton.setText(rb.getString("getAppointment"));
        addButton.setText(rb.getString("addCustomer"));
        modifyButton.setText(rb.getString("modifyCustomer"));
        deleteButton.setText(rb.getString("deleteCustomer"));
        summaryButton.setText(rb.getString("summary"));
        logButton.setText(rb.getString("openLog"));
        exitButton.setText(rb.getString("exit"));
        //customer table view
        nameColumn.setText(rb.getString("name"));
        addressColumn.setText(rb.getString("address"));
        address2Column.setText(rb.getString("address2"));
        cityColumn.setText(rb.getString("city"));
        countryColumn.setText(rb.getString("country"));
        phoneColumn.setText(rb.getString("phone"));
        //appointment table view Month
        monthViewTab.setText(rb.getString("month"));
        apptTitle.setText(rb.getString("title"));
        apptDescription.setText(rb.getString("description"));
        apptLocation.setText(rb.getString("location"));
        apptContact.setText(rb.getString("contact"));
        apptDate.setText(rb.getString("dateTime"));
          //appointment table view Week
          weekViewTab.setText(rb.getString("week"));
        apptTitleWeek.setText(rb.getString("title"));
        apptDescriptionWeek.setText(rb.getString("description"));
        apptLocationWeek.setText(rb.getString("location"));
        apptContactWeek.setText(rb.getString("contact"));
        apptDateWeek.setText(rb.getString("dateTime"));
    }
}

