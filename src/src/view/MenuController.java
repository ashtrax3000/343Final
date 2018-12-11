package view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import calendar.calendar;
import model.user;


public class MenuController {
    
    @FXML
    private MenuItem logoutUser;
    
    private calendar mainApp;
    private user currentUser;
    
    public MenuController() {
        
    }
    
    /**
     * Initializes Menu
     * @param mainApp
     * @param currentUser 
     */
    public void setMenu(calendar mainApp, user currentUser) {
	this.mainApp = mainApp;
        this.currentUser = currentUser;
        
        logoutUser.setText("Logout: " + currentUser.getUsername());
    }    

    @FXML
    void handleMenuAppointments(ActionEvent event) {
        mainApp.showAppointmentScreen(currentUser, 0);
    }
    
    @FXML
    void handleMenuAppointmentsByDay(ActionEvent event) {
        mainApp.showAppointmentScreen(currentUser, 3);
    }
    
    @FXML
    void handleMenuAppointmentsByWeek(ActionEvent event) {
        mainApp.showAppointmentScreen(currentUser, 2);
    }

    @FXML
    void handleMenuAppointmentsByMonth(ActionEvent event) {
        mainApp.showAppointmentScreen(currentUser, 1);
    }
    
    @FXML
    void handleMenuCustomers(ActionEvent event) {
        //mainApp.showCustomerScreen(currentUser);
    }
    
    @FXML
    void handleMenuReports(ActionEvent event) {
        //mainApp.showReports(currentUser);

    }
    
    @FXML
    void handleMenuLogout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Logout");
            alert.setHeaderText("Are you sure you want logout?");
            alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> mainApp.showLoginScreen());
    }
    
    @FXML
    void handleMenuClose(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Close");
            alert.setHeaderText("Are you sure you want close the program?");
            alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent((ButtonType response) -> {
                Platform.exit();
                System.exit(0);
                }
            );     
    }    

}
