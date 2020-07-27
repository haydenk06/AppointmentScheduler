package ViewController;

import Model.Customer;
import static Model.DBManager.DB_URL;
import static Model.DBManager.pass;
import static Model.DBManager.user;
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
 *
 * @author kelsey hayden
 */

public class AddCustomerScreenController implements Initializable {
    @FXML
    private Label titleLabel;
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
    
    @Override public void initialize(URL url, ResourceBundle rb) {
       setLanguage();
    }    
    
    //Handles saving new customer info
    @FXML void saveButtonHandler(ActionEvent event) throws IOException, SQLException { 
    
                //getting entered info fom text fields 
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
                alert.setHeaderText(rb.getString("errorAddingCustomer"));
                alert.setContentText(errorMessage);
                alert.showAndWait();
                return;
            }
            try {
            // If error message is empty, add customer to database and return to main screen
                
                //adding Customer
                 addCustomer(name, address, address2, city, country, postalCode, phone);

                //displaying MainScreen
                Stage stage; 
                Parent root;       
                stage=(Stage) saveButton.getScene().getWindow();
                FXMLLoader loader=new FXMLLoader(getClass().getResource(
                        "MainScreen.fxml"));
                root =loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
               
         }
       catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("ErrorAddingCustomer");
            alert.setContentText("blankFields");
            alert.showAndWait();
        }    
    }
        
    //Cancels adding a new customer and displays Main screen.
    @FXML void cancelButtonHandler(ActionEvent event) throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Resources/AddAndModifyCustomerScreens", Locale.getDefault());
        // Cancelling out of the add customer screen
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(resourceBundle.getString("confirmCancel"));
        alert.setHeaderText(resourceBundle.getString("confirmCancel"));
        alert.setContentText(resourceBundle.getString("confirmCancelAddMessage"));
        Optional<ButtonType> result = alert.showAndWait();
        // If the 'OK' button is clicked, return to main screen
        if(result.get() == ButtonType.OK) {
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
    }
    
//Checks if  customer already exists and adds a new entry itno database
    public static void addCustomer(String customerName, String address, String address2, 
                                        String city, String country, String postalCode, String phone) throws SQLException{
        
        //Getting country, city, and addres ID's
        try{
            int countryId = determineCountryId(country);
            int cityId = determineCityId(city, countryId);
            int addressId = determineAddressId(address, address2, postalCode, phone, cityId);
            
            //checking if customer is already in databse
            if(checkIfExistingCustomer(customerName, addressId)) {
                // TRY WITH Resources block for database connection
                try(Connection conn = DriverManager.getConnection(DB_URL, user, pass);
                        Statement statement = conn.createStatement()) {
                    ResultSet resultSet = statement.executeQuery("SELECT active FROM customer Where " + "customerName = '" 
                                                                      + customerName + "' AND addressId = " + addressId);
                    resultSet.next();
                    
                    int active = resultSet.getInt(1);
                    //Checking to see if customer is active or inactive
                    if(active == 1){
                        // showing an alert if customer is already active
                        ResourceBundle resurcebundle = ResourceBundle.getBundle("Resources/AddAndModifyCustomerScreens", Locale.getDefault());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(resurcebundle.getString("error"));
                        alert.setHeaderText(resurcebundle.getString("errorAddingCustomer"));
                        alert.setContentText(resurcebundle.getString("errorCustomerAlreadyExists"));
                        alert.showAndWait();
                    } else if (active == 0) {
                        //Setting Customer to active if inactive
                        setActive(customerName, addressId);
                    }
                }
            }
            // adding customer as NEW if they don't exist
            else{
                addNewCustomer(customerName, addressId);
            }  
        }
        catch(SQLException e) {
            //showing an alert to user that a database connection is required
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Resources/AddAndModifyCustomerScreens", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(resourceBundle.getString("error"));
            alert.setHeaderText(resourceBundle.getString("errorAddingCustomer"));
            alert.setContentText(resourceBundle.getString("errorRequiresDatabase"));
            alert.showAndWait();
        }
    }
    
    // adding a new customer to database and setting customerId value
    private static void addNewCustomer(String customerName, int addressId) throws SQLException {
//         TRY WITH Resources block for database connection
        try (Connection conn = DriverManager.getConnection(DB_URL,user, pass);
                Statement statement = conn.createStatement()) {
            ResultSet results = statement.executeQuery("SELECT customerId FROM customer ORDER BY customerId");
            int customerId;
            //Check the last customerId value and add 1 for new customer
            if (results.last()) {
                customerId = results.getInt(1) + 1;
                results.close();
            }// if no customers set value to 1
            else{
                results.close();
                customerId = 1;
            }
            String currentUser = null;
            //creating a new entry
           statement.executeUpdate("INSERT INTO customer VALUES (" + customerId + ", '" + customerName + "', " + addressId + ", 1, "
                                                                    +"CURRENT_DATE, '" + currentUser + "', CURRENT_TIMESTAMP, '" 
                                                                    + currentUser + "')");
        }
    }
    
    // Changing customer from inactive to Active
    public static void setActive(String customerName, int addressId) throws SQLException {
        // TRY WITH Resources block for database connection
        try (Connection conn = DriverManager.getConnection(DB_URL,user, pass);
                Statement statement = conn.createStatement()) {
            
            ResourceBundle resourceBundle = ResourceBundle.getBundle("Resources/AddAndModifyScreens", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle(resourceBundle.getString("error"));
            alert.setHeaderText(resourceBundle.getString("errorModifyingCustomer"));
            alert.setContentText(resourceBundle.getString("errorSetToActive"));
            Optional<ButtonType> result = alert.showAndWait();
            //if OK is clicked, set customer to active
            if (result.get() == ButtonType.OK) {
                String currentUser = null;
                statement.executeUpdate("UPDATE customer SET active = 1, lastUpdate = CURRENT_TIMESTAMP, "
                                             + "lastUpdateBy = '" + currentUser + "' WHERE customerName = '" 
                                             + customerName + "' AND addressId = " + addressId);
            }   
        }
    }
    
    // Returning countryId if entry already exists. Creates a new entry if non-existant
    public static int determineCountryId(String country) throws SQLException {
        // TRY WITH Resources block for database connection
        try (Connection conn = DriverManager.getConnection(DB_URL,user, pass);
                Statement statement = conn.createStatement()) {
            ResultSet results = statement.executeQuery("SELECT countryId FROM country WHERE country = '" + country + "'");
            if (results.next()) {
                int countryId = results.getInt(1);
                results.close();
                return countryId;
            }
            else {
                results.close();
                
                int countryId;
                ResultSet allCountryId = statement.executeQuery("SELECT countryId FROM country ORDER BY countryId");
                // checking last id value and adding 1 for new value
                if(allCountryId.last()){ 
                      countryId = allCountryId.getInt(1) + 1;
                      allCountryId.close();
                }
                // if no entries set value to 1
                else {
                    allCountryId.close();
                    countryId = 1;
                }
                String currentUser = null;
                //Creating new entry with new id value
                statement.executeUpdate("INSERT INTO country Values (" + countryId + ", '" + country + "', CURRENT_DATE, " 
                                            + "'" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
                return countryId;
            }
        
        }
    }
    
    //returning cityId if entry already exists. Creates a new entry if non-existant
    public static int determineCityId(String city, int countryId) throws SQLException {
        // TRY WITH Resources block for database connection
        try (Connection conn = DriverManager.getConnection(DB_URL,user, pass);
                Statement statement = conn.createStatement()) {
            ResultSet results = statement.executeQuery("SELECT cityId FROM city WHERE city = '" + city + "' AND countryid = " + countryId);
            
            //check if entry already exists and return Id
            if(results.next()) {
                int cityId = results.getInt(1);
                results.close();
                return cityId;
            } else {
                    results.close();
                    
                    int cityId;
                    ResultSet allCityId = statement.executeQuery("SELECT cityId FROM city ORDER BY cityId");
                    //Checking last value and adding 1 for new value
                    if (allCityId.last()) {
                        cityId = allCityId.getInt(1) + 1;
                        allCityId.close();
                    }
                    //no values, set to 1
                    else {
                     allCityId.close();
                     cityId = 1;
                    }
                String currentUser = null;
                    // creating new entry with value
                    statement.executeUpdate("INSERT INTO city VALUES (" + cityId + ", '" + city + "', " + countryId + ", CURRENT_DATE, " 
                                                  + "'" + currentUser + "', CURRENT_TIMESTAMP, '" + currentUser + "')");
                    return cityId;
            }
        }
    }
    
     //Returning addressId if entry already exists. Creates a new entry if non-existant
    public static int determineAddressId(String address, String address2, String postalCode, String phone, int cityId) throws SQLException {
        // TRY WITH Resources block for database connection
        try (Connection conn = DriverManager.getConnection(DB_URL,user, pass);
                Statement statement = conn.createStatement()) {
            ResultSet results = statement.executeQuery("SELECT addressId FROM address WHERE address = '" + address + "' AND " 
                                                            + "address2 = '" + address2 + "' AND postalCode = '" + postalCode 
                                                            + "' AND phone = '" + phone + "' AND cityId = " + cityId);
            //check if entry already exists and return Id
            if(results.next()) {
                int addressId = results.getInt(1);
                results.close();
                return addressId;
            }
            else{
                results.close();
                
                int addressId;
                ResultSet allAddressId = statement.executeQuery("SELECT addressId FROM address ORDER BY addressId");
                //Checking last value and adding 1 for new value
                if (allAddressId.last()) {
                    addressId = allAddressId.getInt(1) + 1;
                    allAddressId.close();
                }
                //if no values, set id to 1
                else{
                    allAddressId.close();
                    addressId = 1;
                }
                String currentUser = null;
                //Create new entry with id value
                statement.executeUpdate("INSERT INTO address VALUES (" + addressId + ", '" + address + "', '" + address2 + "', " 
                                                                      + cityId + ", " + "'" + postalCode + "', '" + phone + "', "
                                                                      + " CURRENT_DATE, '" + currentUser + "', CURRENT_TIMESTAMP, '" 
                                                                      + currentUser + "')");
                return addressId;
            }
        }
    }
    
 // checking if customer already exists in database
    public static boolean checkIfExistingCustomer(String customerName, int addressId) throws SQLException {
        // TRY WITH Resources block for database connection
        try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
                Statement statement = conn.createStatement()) {
            ResultSet results = statement.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + customerName + "' " 
                                                                   + "AND addressId = " + addressId);
            
            //checking if entry already exists and returning a boolean value
            if (results.next()) {
                results.close();
                return true;
            } 
            else {
                results.close();
                return false;
            }
        }
    }
    
     //changing language based on computer locale default
@FXML private void setLanguage(){
        ResourceBundle rb = ResourceBundle.getBundle("Resources/AddAndModifyCustomerScreens", Locale.getDefault());
        titleLabel.setText(rb.getString("addCustomer"));
        nameLabel.setText(rb.getString("name"));
        nameTextField.setPromptText(rb.getString("name"));
        addressLabel.setText(rb.getString("address"));
        addressTextField.setPromptText(rb.getString("address"));
        address2Label.setText(rb.getString("address2"));
        address2TextField.setPromptText(rb.getString("address2"));
        cityLabel.setText(rb.getString("city"));
        cityTextField.setPromptText(rb.getString("city"));
        countryLabel.setText(rb.getString("country"));
        countryTextField.setPromptText(rb.getString("country"));
        postalCodeLabel.setText(rb.getString("postalCode"));
        postalCodeTextField.setPromptText(rb.getString("postalCode"));
        phoneLabel.setText(rb.getString("phone"));
        phoneTextField.setPromptText(rb.getString("phone"));
        saveButton.setText(rb.getString("save"));
        cancelButton.setText(rb.getString("cancel"));  
    }
}
