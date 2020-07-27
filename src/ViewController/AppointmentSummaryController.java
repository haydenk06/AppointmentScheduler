package ViewController;

import Model.Appointment;
import Model.AppointmentList;
import static Model.AppointmentList.getAppointmentList;
import static Model.AppointmentList.updateAppointmentList;
import static Model.DBManager.DB_URL;
import static Model.DBManager.pass;
import static Model.DBManager.user;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kelsey hayden
 */

public class AppointmentSummaryController implements Initializable {

    @FXML
    private TableView<Appointment> summaryTable;
    @FXML
    private TableColumn<Appointment, String> titleColumn;
    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> contactColumn;
    @FXML
    private Button getInfoButton;
     @FXML
    private Button addAppointmentButton;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;
    @FXML 
    private Button mainButton;
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private Label location;
    @FXML
    private Label contact;
    @FXML
    private Label url;
    @FXML
    private Label date;
    @FXML
    private Label startTime;
    @FXML
    private Label endTime;
    @FXML
    private Label createdBy;
    
     // Holds index of the appointment that was selected to modify
    private static int appointmentIndexToModify;
  
    
    @Override public void initialize(URL url, ResourceBundle rb) {
        setLanguage();
        
        //Lambda Expression assiging data to the table
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateStringProperty());
        contactColumn.setCellValueFactory(cellData -> cellData.getValue().contactProperty());
        
        //shows appointmentList on table and updates when neede
        updateAppointmentTableView();
    }    
    
    // cancels out of the add appointment screen and displays main screen
    @FXML private void mainButtonHandler(ActionEvent event){
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Resources/AppointmentSummary", Locale.getDefault());
        // Cancelling out of the add summary screen
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initModality(Modality.NONE);
        alert.setTitle(resourceBundle.getString("confirm"));
        alert.setHeaderText(resourceBundle.getString("confirm"));
        alert.setContentText(resourceBundle.getString("confirmMain"));
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

    //Gets appointment informationa nd displays it below table
    @FXML private void getInfoButtonHandler(ActionEvent event) {
        Appointment appointment = summaryTable.getSelectionModel().getSelectedItem();
        ResourceBundle rb = ResourceBundle.getBundle("Resources/AppointmentSummary", Locale.getDefault());

        if (appointment == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("gettingInfo"));
            alert.setContentText(rb.getString("pleaseSelect"));
            alert.showAndWait();
            
        } else {
            title.setText(rb.getString("title") + ": " + appointment.getTitle());
            description.setText(rb.getString("description") + ": " + appointment.getDescription());
            location.setText(rb.getString("location") + ": " + appointment.getLocation());
            contact.setText(rb.getString("contact") + ": " + appointment.getContact());
            url.setText(rb.getString("url") + ": " + appointment.getUrl());
            date.setText(rb.getString("date") + ": " + appointment.getDateString());
            startTime.setText(rb.getString("startTime") + ": " + appointment.getStartString());
            endTime.setText(rb.getString("endTime") + ": " + appointment.getEndString());
            createdBy.setText(rb.getString("createdBy") + ": " + appointment.getCreatedBy());
        }
    }
    
     // Handles adding  new appointments
    @FXML void addAppointmentButtonHandler(ActionEvent event) throws IOException{
         //displaying addAppointment screen
        Stage stage; 
        Parent root;       
        stage=(Stage) addAppointmentButton.getScene().getWindow();
        FXMLLoader loader=new FXMLLoader(getClass().getResource(
               "AddAppointmentScreen.fxml"));
        root =loader.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

        // opens the modify appoinment screen
    @FXML private void modifyButtonHandler(ActionEvent event) {
        //gets selected appoinment to modify
        Appointment appointmentToModify = summaryTable.getSelectionModel().getSelectedItem();
        //checks if an appointment was selected
        if (appointmentToModify == null) {
            ResourceBundle rb = ResourceBundle.getBundle("Resources/AppointmentSummary", Locale.getDefault());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(rb.getString("error"));
            alert.setHeaderText(rb.getString("modifyingAppt"));
            alert.setContentText(rb.getString("pleaseSelect"));
            alert.showAndWait();
            return;
        }
          // Set the index of the appointment that was selected to be modified
        appointmentIndexToModify = getAppointmentList().indexOf(appointmentToModify);
        try {
            Parent mainScreenParent = FXMLLoader.load(getClass().getResource("ModifyAppointment.fxml"));
                Scene mainScreenScene = new Scene(mainScreenParent);
                Stage mainScreenStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                mainScreenStage.setScene(mainScreenScene);
                mainScreenStage.show();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // deletes appoinment from database and updates appointmentList and table
    @FXML private void deleteButtonHandler(ActionEvent event) {
        //gets selected appoinment from table view
        Appointment appointment = summaryTable.getSelectionModel().getSelectedItem();
        ResourceBundle rb = ResourceBundle.getBundle("Resources/AppointmentSummary", Locale.getDefault());
        // Checks if appointment was selected
        if (appointment == null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle(rb.getString("error"));
                alert.setHeaderText(rb.getString("deletingAppt"));
                alert.setContentText(rb.getString("deleteMessage"));
                alert.showAndWait();
                return;
        }
                int appointmentId = appointment.getAppointmentId();
                // Try-with-resources block for database connection
                try (Connection conn = DriverManager.getConnection(DB_URL,user, pass);
                     Statement statement = conn.createStatement()) {
                    statement.executeUpdate("DELETE FROM appointment WHERE appointmentId =" + appointmentId);
                }   catch (SQLException ex) {
                        Logger.getLogger(AppointmentSummaryController.class.getName()).log(Level.SEVERE, null, ex);
                }
                // Update appointmentList to remove deleted appointment
                updateAppointmentList();
        }

     //loads appointmentList to table when screen is opened and updates when called
    @FXML public void updateAppointmentTableView() {
        updateAppointmentList();
        summaryTable.setItems(AppointmentList.getAppointmentList());
    }
    
       //Gets appointment info to be modified and sends it to modify screen
    @FXML  public static int getAppointmentIndexToModify() {
            return appointmentIndexToModify;
    }
    
    @FXML private void setLanguage() {
        ResourceBundle rb = ResourceBundle.getBundle("Resources/AppointmentSummary", Locale.getDefault());
        title.setText(rb.getString("title"));
        titleColumn.setText(rb.getString("title"));
        date.setText(rb.getString("date"));
        dateColumn.setText(rb.getString("date"));
        contact.setText(rb.getString("contact"));
        contactColumn.setText(rb.getString("contact"));
        getInfoButton.setText(rb.getString("getInfo"));
        addAppointmentButton.setText(rb.getString("add"));
        modifyButton.setText(rb.getString("modify"));
        deleteButton.setText(rb.getString("delete"));
        mainButton.setText(rb.getString("main"));
        description.setText(rb.getString("description"));
        location.setText(rb.getString("location"));
        contact.setText(rb.getString("contact"));
        url.setText(rb.getString("url"));
        date.setText(rb.getString("date"));
        startTime.setText(rb.getString("startTime"));
        endTime.setText(rb.getString("endTime"));
        createdBy.setText(rb.getString("createdBy"));
    }
}
