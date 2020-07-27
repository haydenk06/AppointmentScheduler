package ViewController;

//import static Model.DBManager.checkLogIn;
import Model.UserDB;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import java.sql.SQLException;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author kelsey hayden
 */
public class LoginScreenController implements Initializable {
    @FXML
    private Label passwordLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label loginHintLabel;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private TextField usernameTextField;
    @FXML
    private Button exitButton;
    @FXML
    private Button loginButton;
    @FXML
    private Label intro;
    @FXML
    private Label title;
    @FXML
    private TextField passwordTextField;
    
    public static int databaseError = 0;

    /**
     * Initializes the controller class.
     */
    public void initialize(URL url, ResourceBundle rb) {
        setLanguage();
    }    

      //Handles exiting of application
    @FXML private void exitButtonHandler(ActionEvent event) throws SQLException, Exception{
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initModality(Modality.NONE);
            alert.setTitle("Confirmation Dialog");
            alert.setHeaderText("Ready?");
            alert.setContentText("Are you sure you want to Exit?");
            Optional<ButtonType> result = alert.showAndWait();
        //closing database connection + application
        if (result.get() == ButtonType.OK) {
            //DBManager.closeConnection();
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
            System.exit(0);
        }
    }
    
    //Handles login. Checks for no blank fields.
    @FXML private void loginButtonHandler(ActionEvent event)throws IOException, ClassNotFoundException, SQLException {
        //username = test
        //password = test
        //gets user input and clears password field
        String userName = usernameTextField.getText();
        String password = passwordTextField.getText();
        //clears password
        passwordTextField.setText("");
        ResourceBundle rb = ResourceBundle.getBundle("Resources/Login", Locale.getDefault());
        
        //checking if text was entered into field. if blank returns error
        if (userName.equals("") || password.equals("")) {
            loginHintLabel.setText(rb.getString("emptyFields"));
            return;
        }
        
        // loading main screen if username + password are correct while checking against database.
       // boolean correctSignIn = checkLogIn(userName, password);
       boolean correctSignIn = UserDB.login(userName, password);
        if (correctSignIn) {
            try{
                Parent parent = FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
                Scene scene = new Scene(parent);
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }catch(IOException e){
                e.printStackTrace();
            }
        
        } else if (databaseError > 0) {
            //shows connection error for database
            errorMessageLabel.setText(rb.getString("serverError"));
       
        } else {
            // shows eror for incorrect username/password
            loginHintLabel.setText(rb.getString("wrongUserPass"));
        }
    }
    
    @FXML public static void incrementDatabaseError() {
        databaseError++;
    }
    
    //changing language based on computer locale default
    private void setLanguage(){
        ResourceBundle rb = ResourceBundle.getBundle("Resources/Login", Locale.getDefault());
        title.setText(rb.getString("title"));
        intro.setText(rb.getString("intro"));
        usernameLabel.setText(rb.getString("usernameLabel"));
        passwordLabel.setText(rb.getString("passwordLabel"));
        loginButton.setText(rb.getString("loginButton"));
        exitButton.setText(rb.getString("exitButton"));
    }
}