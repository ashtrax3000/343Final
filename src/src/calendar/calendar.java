package calendar;

import util.dbconnection;
import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.appointment;
import model.user;
import view.*;

public class calendar extends Application {
    
    private Stage primaryStage;
    private BorderPane menu;
    private AnchorPane loginScreen;
    private AnchorPane customerScreen;
    private AnchorPane appointmentScreen;
    private AnchorPane custReportScreen;
    private TabPane tabPane;
    Locale locale = Locale.getDefault();
    private static Connection connection;

    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Calendar Application");
        showLoginScreen();
    }
   
    /**
     * calls inti() for database
     * runs args then calls closeConn() to close database connections
     * @param args 
     */
    public static void main(String[] args) {
        
        dbconnection.init();
        
        connection = dbconnection.getConn();
        launch(args);
        dbconnection.closeConn();
    }
    
    
    
    /**
     * Calls login screen
     */
    public void showLoginScreen() {
        try {
            // Load Login Screen.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(calendar.class.getResource("/view/LoginScreen.fxml"));
            AnchorPane loginScreen = (AnchorPane) loader.load();
            
            // Give the controller access to the main app.
            LoginScreenController controller = loader.getController();
            controller.setLogin(this);            
            
            // Show the scene containing the root layout.
            Scene scene = new Scene(loginScreen);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the root menu.
     * @param currentUser
     */
    public void showMenu(user currentUser) {
        
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(calendar.class.getResource("/view/Menu.fxml"));
            menu = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(menu);
            primaryStage.setScene(scene);
            // Give the controller access to the main app.
            MenuController controller = loader.getController();
            controller.setMenu(this, currentUser);
            
            primaryStage.show();
        } catch (IOException e) {
            e.getCause().printStackTrace();
        }
    }
    
        /**
     * Calls appointment Screen and passes in currentUser
     * @param currentUser 
     */
    public void showAppointmentScreen(user currentUser, int set) {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(calendar.class.getResource("/view/AppointmentScreen.fxml"));
            AnchorPane appointmentScreen = (AnchorPane) loader.load();

            // Set person overview into the center of root layout.
            menu.setCenter(appointmentScreen);

            // Give the controller access to the main app.
            AppointmentScreenController controller = loader.getController();
            controller.setAppointmentScreen(this, currentUser, set);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Calls New appointment Screen, passes currentUser, and returns false, 
     * so subsequent save is done as new appointment
     * @param currentUser
     * @return 
     */
    
    public boolean showNewApptScreen(user currentUser) {
    try {
        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(calendar.class.getResource("/view/AppointmentEditScreen.fxml"));
        AnchorPane newApptScreen = (AnchorPane) loader.load();

        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle("New Appointment");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(newApptScreen);
        dialogStage.setScene(scene);

        // Set the person into the controller.
        AppointmentEditScreenController controller = loader.getController();
        controller.setDialogStage(dialogStage, currentUser);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();

        return controller.isOkClicked();
        } catch (IOException e) {
        e.printStackTrace();
        return false;
        }
    }
    
    
    public boolean showEditApptScreen(appointment appointment, user currentUser) {
    try {
        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(calendar.class.getResource("/view/AppointmentEditScreen.fxml"));
        
        AnchorPane editApptScreen = (AnchorPane) loader.load();

        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Edit Appointment");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(editApptScreen);
        dialogStage.setScene(scene);

        // Set the person into the controller.
        AppointmentEditScreenController controller = loader.getController();
        controller.setDialogStage(dialogStage, currentUser);
        controller.setAppointment(appointment);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();

        return controller.isOkClicked();
        } catch (IOException e) {
        e.printStackTrace();
        return false;
        }
    }    
}
