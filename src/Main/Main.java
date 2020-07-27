package Main;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



/**
 *
 * @author kelsey
 */
public class Main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        //Locale.setDefault(new Locale.Builder().setLanguage("de").build());
        //TimeZone.setDefault(TimeZone.getTimeZone("PST"));
        Parent root = FXMLLoader.load(getClass().getResource("/ViewController/LoginScreen.fxml")); 
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
