package Model;

import static Model.DBManager.DB_URL;
import static Model.DBManager.conn;
import static Model.DBManager.pass;
import static Model.DBManager.user;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

/**
 *
 * @author kelsey hayden
 */
public class CustomerList {
        private static ObservableList<Customer> customerList = FXCollections.observableArrayList();

        public static ObservableList<Customer> getCustomerList() {
            return customerList;
        }
    
        //set customer to inactive when deleted - main screen
         public static void customerToInactive(Customer customer) {
                    int customerId = customer.getCustomerId();
                    ResourceBundle resourceBundle = ResourceBundle.getBundle("Resources.DBManager", Locale.getDefault());

                     Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                     alert.initModality(Modality.NONE);
                     alert.setTitle(resourceBundle.getString("remove"));
                     alert.setHeaderText(resourceBundle.getString("removingCustomer"));
                     alert.setContentText(resourceBundle.getString("removingCustomerMsg"));
                     Optional<ButtonType> result = alert.showAndWait();
                      if (result.get() == ButtonType.OK) {
                                try(Statement statement = conn.createStatement();) {
                                statement.executeUpdate("UPDATE customer SET active = 0 WHERE customerId = " + customerId);

                       } catch (SQLException e) {
                               Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                               alert2.setTitle(resourceBundle.getString("error"));
                               alert2.setHeaderText(resourceBundle.getString("errorModifyingCustomer"));
                               alert2.setContentText(resourceBundle.getString("errorRequiresDatabase"));
                               alert2.showAndWait();
                          }
                               updateCustomerList();
                     }
     }
    
          //Updating Customer list after change in the database
         public static void updateCustomerList(){
                   // TRY WITH Resources block for database connection
                    try (Connection conn = DriverManager.getConnection(DB_URL, user, pass);
                     Statement statement = conn.createStatement()) {

                              //retrieve customerList and clear
                             ObservableList<Customer> customerList = getCustomerList();
                             customerList.clear();

                             //creates list of customerId's for all customers that are active
                             ResultSet resultSet = statement.executeQuery("SELECT customerId FROM customer WHERE active = 1");
                             ArrayList<Integer> customerIdList = new ArrayList<>();

                              while (resultSet.next()){
                                         customerIdList.add(resultSet.getInt(1));       
                             }

                              // Creates a customer object for each id in results and adds Customer to list
                              for (int customerId : customerIdList) {
                                     //creates new object for customer
                                     Customer customer  = new Customer();

                                 //gets information from database and adds to customer object
                                 ResultSet customerResults = statement.executeQuery("SELECT customerName, active, addressId FROM customer WHERE customerId = " + customerId);
                                 customerResults.next();
                                 String customerName = customerResults.getString(1);
                                 int active = customerResults.getInt(2);
                                 int addressId = customerResults.getInt(3);
                                 customer.setCustomerId(customerId);
                                 customer.setCustomerName(customerName);
                                 customer.setActive(active);
                                 customer.setAddressId(addressId);

                                 //gets address information from database and adds to customer object
                                 ResultSet addressResults = statement.executeQuery("SELECT address, address2, postalCode, phone, cityId FROM address WHERE addressId = " + addressId);
                                 addressResults.next();
                                 String address = addressResults.getString(1);
                                 String address2 = addressResults.getString(2);
                                 String postalCode = addressResults.getString(3);
                                 String  phone = addressResults.getString(4);
                                 int cityId = addressResults.getInt(5);
                                 customer.setAddress(address);
                                 customer.setAddress2(address2);
                                 customer.setPostalCode(postalCode);
                                 customer.setPhone(phone);
                                 customer.setCityId(cityId);

                                 //gets city information from databse and adds to customer object
                                 ResultSet cityResults = statement.executeQuery("SELECT city, countryId FROM city WHERE cityId = " + cityId);
                                 cityResults.next();
                                 String city = cityResults.getString(1);
                                 int countryId = cityResults.getInt(2);
                                 customer.setCity(city);
                                 customer.setCountryId(countryId);

                                 //gets country information from database and adds to customer object
                                 ResultSet countryResults = statement.executeQuery("SELECT country FROM country WHERE countryId = " + countryId);
                                 countryResults.next();
                                 String country = countryResults.getString(1);
                                 customer.setCountry(country);

                                 //adds new customer object to customerList
                                 customerList.add(customer);
                         }
                    } catch (SQLException e) {
                        ResourceBundle resourceBundle = ResourceBundle.getBundle("Resources/MainScreen", Locale.getDefault());
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(resourceBundle.getString("error"));
                        alert.setHeaderText(resourceBundle.getString("error"));
                        alert.setContentText(resourceBundle.getString("errorRequiresDatabase"));
                        alert.show();
                     }
         }
}
