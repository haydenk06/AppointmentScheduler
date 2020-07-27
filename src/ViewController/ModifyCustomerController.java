package ViewController;

import Model.Customer;
import static Model.CustomerList.getCustomerList;
import static Model.DBManager.DB_URL;
import static Model.DBManager.pass;
import static Model.DBManager.user;
import static ViewController.AddCustomerScreenController.determineAddressId;
import static ViewController.AddCustomerScreenController.determineCityId;
import static ViewController.AddCustomerScreenController.determineCountryId;
import static ViewController.AddCustomerScreenController.checkIfExistingCustomer;
import static ViewController.AddCustomerScreenController.setActive;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kelsey hayden
 */
public class ModifyCustomerController implements Initializable {
     @FXML
    private Label title;
    @FXML
    private Label nameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label address2Label;
    @FXML
    private Label cityLabel;
    @FXML
    private Label countryLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField addressTextField;
    @FXML
    private TextField address2TextField;
    @FXML
    private TextField cityTextField;
    @FXML
    private TextField countryTextField;
    @FXML
    private TextField postalCodeTextField;
    @FXML
    private TextField phoneTextField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    
    // Initialize customer object
    private Customer customer;
    
    // Get index of customer to be modified
    int customerIndexToModify  = MainScreenController.customerIndexToModify ();
    
    //used for update customer
    private static String currentUser;
    
    @Override public void initialize(URL url, ResourceBundle rb) {
       ///// //Loads customer information into textfields/////
         // Get customer to be modified via index
        customer = getCustomerList().get(customerIndexToModify );
        // Get customer information
        String name = customer.getCustomerName();
        String address = customer.getAddress();
        String address2 = customer.getAddress2();
        String city = customer.getCity();
        String country = customer.getCountry();
        String postalCode = customer.getPostalCode();
        String phone = customer.getPhone();
        // Populate information fields with current customer information
        nameTextField.setText(name);
        addressTextField.setText(address);
        address2TextField.setText(address2);
        cityTextField.setText(city);
        countryTextField.setText(country);
        postalCodeTextField.setText(postalCode);
        phoneTextField.setText(phone);
    }    
    
    //updates information in database for customer
    @FXML private void saveButtonHandler(ActionEvent event) throws SQLException {
        // Getting customer info from text fields
        int customerId = customer.getCustomerId();
        String name = nameTextField.getText();
        String address = addressTextField.getText();
        String address2 = address2TextField.getText();
        String city = cityTextField.getText();
        String country = countryTextField.getText();
        String postalCode = postalCodeTextField.getText();
        String phone = phoneTextField.getText();
        
              // Check if all fields are valid
            String errorMessage = Customer.isCustomerValid(name, address, city, country, postalCode, phone);
            // If error message contains something, create error message box
            if (errorMessage.length() > 0) {
                ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyCustomerScreens", Locale.getDefault());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("errorModifyingCustomer"));
                alert.setContentText(errorMessage);
                alert.showAndWait();
                return;
            }
            try {
            ////// If error message is empty, add customer to database and return to main screen//////
            // Submit information to be updated in database. Save active status that is returned
            int modifyCustomerCheck = modifyCustomer(customerId, name, address, address2, city, country, postalCode, phone);
            // Check if active status is 1
            if (modifyCustomerCheck == 1) {
                ResourceBundle rb = ResourceBundle.getBundle("AddModifyCustomer", Locale.getDefault());
                // Create alert saying that customer already exists
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("errorModifyingCustomer"));
                alert.setContentText(rb.getString("errorCustomerAlreadyExists"));
                alert.showAndWait();

            }
            // Check if active status is 0
            else if (modifyCustomerCheck == 0) {
                // Calculate country, city and addressId's
                int countryId = determineCountryId(country);
                int cityId = determineCityId(city, countryId);
                int addressId = determineAddressId(address, address2, postalCode, phone, cityId);
                // Submit customer to be set to active
                setActive(name, addressId);
            }

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
 
    //Cancels out of modify customer screen and displays main screen
    @FXML private void cancelButtonHandler(ActionEvent event) {
        ResourceBundle rb = ResourceBundle.getBundle("Resources.AddAndModifyCustomerScreens", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(rb.getString("confirmCancel"));
        alert.setHeaderText(rb.getString("confirmCancel"));
        alert.setContentText(rb.getString("confirmCancelModifyMessage"));
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
            }
        }
    }
    
//     used to update customer information in databasse
    public static int modifyCustomer(int customerId, String name, String address, String address2, 
                                        String city, String country, String postalCode, String phone) {
        try{
            //finding customers country, city and addressId
            int countryId = determineCountryId(country);
            int cityId = determineCityId(city, countryId);
            int addressId = determineAddressId(address, address2, postalCode,phone, cityId);
            
            //checking if customer exists
            if(checkIfExistingCustomer(name, addressId)){
                //if customer exists, get id and use id to return status
                int existingCustomerId = getCustomerId(name, addressId);
                int activeStatus = getStatus(existingCustomerId);
                return activeStatus;
            } else{
                // if customer does not exist, update customer entry and clear database of unused entries
                updateCustomer(customerId, name, addressId);
                return -1;
            }
        } catch(SQLException e){
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Resources/AddAndModifyCustomerScreens", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resourceBundle.getString("error"));
            alert.setHeaderText(resourceBundle.getString("errorModifyingCustomer"));
            alert.setContentText(resourceBundle.getString("errorRequiresDatabase"));
            alert.show();
            return -1;
        }
    }
    
 
    
    
    //Getting customerId from name and addressId
    private static int getCustomerId(String name, int addressId) throws SQLException {
         // TRY WITH Resources block for database connection
        try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
                Statement statement = conn.createStatement()) {
            
            ResultSet resultSet = statement.executeQuery("SELECT customerId FROM customer WHERE name = '" + name + "' "
                                                             + "AND addressId = " + addressId);
            resultSet.next();
            int customerId = resultSet.getInt(1);
            return customerId;
            
        }
    }
    
    //gets inactive/active status of customer 
    private static int getStatus(int customerId) throws SQLException {
         //TRY WITH Resources block for database connection
        try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
                Statement statement = conn.createStatement()) {
            
            ResultSet resultSet = statement.executeQuery("SELECT active FROM customer WHERE customerId = " + customerId);
            resultSet.next();
            int status = resultSet.getInt(1);
            return status;
        }
    }
    
    //Updates customer entry if unique
    private static void updateCustomer(int customerId, String name, int addressId) throws SQLException{
         //TRY WITH Resources block for database connection
        try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
                Statement statement = conn.createStatement()) {
            String currentUser = null;
            
            statement.executeUpdate("UPDATE customer SET customerName = '" + name + "', addressId = " + addressId + ", " 
                                        + "lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = '" + currentUser 
                                        + "' WHERE customerId = " + customerId);
            
        }
    }
}
