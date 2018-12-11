
package view;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import util.dbconnection;
import calendar.calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.scene.control.TextField;
import model.appointment;
import model.customer;
import model.user;

public class AppointmentScreenController {
     
    @FXML
    private TableView<appointment> apptTableView;

    @FXML
    private TableColumn<appointment, String> idApptColumn;
    
    @FXML
    private TableColumn<appointment, String > begintimeApptColumn;

    @FXML
    private TableColumn<appointment, String > endtimeApptColumn;
    
    @FXML
    private TableColumn<appointment, String> titleApptColumn;

    @FXML
    private TableColumn<appointment, String> descriptionApptColumn;

    @FXML
    private TableColumn<appointment, String> locationApptColumn;
    
    private calendar mainApp;
    private user currentUser;
    private int set;
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    //private final ZoneId newzid = ZoneId.systemDefault();
    ObservableList<appointment> apptList;
    
    /**
     * Initializes appointment Screen 
     * @param mainApp
     * @param currentUser 
     */    
    
    public void setAppointmentScreen(calendar mainApp, user currentUser, int set) {
	this.mainApp = mainApp;
        this.currentUser = currentUser;
        this.set = set;
        
        idApptColumn.setCellValueFactory(new PropertyValueFactory<>("appid"));
        begintimeApptColumn.setCellValueFactory(new PropertyValueFactory<>("begintime"));
        endtimeApptColumn.setCellValueFactory(new PropertyValueFactory<>("endtime"));
        titleApptColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionApptColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationApptColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        //reminderApptColumn.setCellValueFactory(new PropertyValueFactory<>("remindertime"));
        
        apptList = FXCollections.observableArrayList();
        populateAppointmentList();
        apptTableView.getItems().setAll(apptList); 
        
    }
    
    /**
     * Filters to show appointments from current date to a month out
     * @param event 
     */
    @FXML
    void handleApptMonth() {
        
        LocalDate now = LocalDate.now();
        LocalDate nowPlus1Month = now.plusMonths(1);
        
        FilteredList<appointment> filteredData = new FilteredList<>(apptList);
        filteredData.setPredicate(row -> {

            LocalDate rowDate = row.getBegintime().toLocalDate();

            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus1Month);
            
        });
        apptTableView.setItems(filteredData);

    }
    
    /**
     * Filters to show appointments from current date to a week out
     * @param event 
     */
    
    @FXML
    void handleApptWeek() {
        
        LocalDate now = LocalDate.now();
        LocalDate nowPlus7 = now.plusDays(7);
        FilteredList<appointment> filteredData = new FilteredList<>(apptList);
        
        filteredData.setPredicate(row -> {

            LocalDate rowDate = row.getBegintime().toLocalDate();

            return rowDate.isAfter(now.minusDays(1)) && rowDate.isBefore(nowPlus7);
        });
        apptTableView.setItems(filteredData);
        


    }
    
    /**
     * Filters to show appointments from current date to a day out
     * @param event 
     */
    @FXML
    void handleApptDay() {
        
        LocalDate now = LocalDate.now();
        LocalDate nowPlus0 = now.plusDays(0);
        FilteredList<appointment> filteredData = new FilteredList<>(apptList);
        filteredData.setPredicate(row -> {

            LocalDate rowDate = row.getBegintime().toLocalDate();

            return rowDate.isEqual(now.minusDays(1)) && rowDate.isBefore(nowPlus0);
        });
        apptTableView.setItems(filteredData);
    }    
    
    
    @FXML
    void handleDeleteAppt(ActionEvent event) {
        appointment selectedAppointment = apptTableView.getSelectionModel().getSelectedItem();
        
        if (selectedAppointment != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete " + selectedAppointment.getTitle() + " scheduled for " + selectedAppointment.getBegintime() + "?");
            alert.showAndWait()
            .filter(response -> response == ButtonType.OK)
            .ifPresent(response -> {
                deleteAppointment(selectedAppointment);
                mainApp.showAppointmentScreen(currentUser, 0);
                }
            );
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Appointment selected for Deletion");
            alert.setContentText("Please select an Appointment in the Table to delete");
            alert.showAndWait();
        }

    }

    @FXML
    void handleEditAppt(ActionEvent event) {
        appointment selectedAppointment = apptTableView.getSelectionModel().getSelectedItem();
        
        if (selectedAppointment != null) {
            boolean okClicked = mainApp.showEditApptScreen(selectedAppointment, currentUser);
            mainApp.showAppointmentScreen(currentUser, 0);
            
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No Appointment selected");
            alert.setContentText("Please select an Appointment in the Table");
            alert.showAndWait();
        }

    }

    @FXML
    void handleNewAppt(ActionEvent event) throws IOException{
        boolean okClicked = mainApp.showNewApptScreen(currentUser);
        mainApp.showAppointmentScreen(currentUser, 0);
    }
    
    private void populateAppointmentList() {
      
        try{            
            
        PreparedStatement statement = dbconnection.getConn().prepareStatement(
        "SELECT * FROM appointments "
                + "WHERE userid = ? "
                + "ORDER BY begintime");
            statement.setInt(1, currentUser.getUserID());
            ResultSet rs = statement.executeQuery();
           
            
            while (rs.next()) {
                
                int tAppId = rs.getInt("appid");
                
                //Timestamp tBegintime = rs.getTimestamp("begintime");
                LocalDateTime tBegintime = rs.getTimestamp("begintime").toLocalDateTime();
                //date.setTime(bt.getTime());
                //String tBegintime = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(date);
                
                
                LocalDateTime tEndtime = rs.getTimestamp("begintime").toLocalDateTime();
                //date.setTime(et.getTime());
                //String tEndtime = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(date);
                
                //ZonedDateTime newzdtEnd = tsEnd.toLocalDateTime().atZone(ZoneId.of("UTC"));
        	//ZonedDateTime newLocalEnd = newzdtEnd.withZoneSameInstant(newzid);
                //Timestamp tReminder = rs.getTimestamp("reminder");
                
                String tTitle = rs.getString("title");
                String tDescription = rs.getString("description");
                String tLocation = rs.getString("location");
                                                      
                apptList.add(new appointment(tAppId, tTitle, tLocation, tDescription, tBegintime, tEndtime));
                System.out.println( "ID # is: " + currentUser.getUserID() + " "  + tBegintime + "  " + tEndtime);
                
            }
            
        } catch (SQLException sqe) {
            System.out.println("Check your SQL");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Something besides the SQL went wrong.");
        }

    }
    
    private void deleteAppointment(appointment appointment) {
        try{           
            PreparedStatement pst = dbconnection.getConn().prepareStatement("DELETE FROM appointments WHERE appid = ?");
            pst.setInt(1, appointment.getAppid()); 
            pst.executeUpdate();  
                
        } catch(SQLException e){
            e.printStackTrace();
        }       
    }

    
    
}
