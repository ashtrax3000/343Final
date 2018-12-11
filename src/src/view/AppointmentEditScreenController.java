
package view;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import util.dbconnection;
import calendar.calendar;
import javafx.scene.control.TextArea;
import model.appointment;
import model.user;

public class AppointmentEditScreenController {

    @FXML
    private Label apptLabel;

    @FXML
    private TextField titleField;

    @FXML
    private TextField locationField;
    
    @FXML
    private TextArea descriptionField;
    
    @FXML
    private ComboBox<String> beginComboBox;

    @FXML
    private ComboBox<String> endComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private Button apptSaveButton;

    @FXML
    private Button apptCancelButton;

    private Stage dialogStage;
    private calendar mainApp;
    private boolean okClicked = false;
    private final ZoneId zid = ZoneId.systemDefault();
    private appointment selectedAppt;
    private user currentUser;
    
    private final ObservableList<String> beginTimes = FXCollections.observableArrayList();
    private final ObservableList<String> endTimes = FXCollections.observableArrayList();
    private final DateTimeFormatter timeDTF = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private final DateTimeFormatter dateDTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);
    ObservableList<appointment> apptTimeList;
    
    /**
     * Returns true if the user clicked OK, false otherwise.
     * Which allows application to decide whether to insert or update a record
     * @return
     */
    
    public boolean isOkClicked() {
        return okClicked;
    }    
    
    
    @FXML
    private void handleSave(ActionEvent event) {
        if (validateAppointment()){
            if (isOkClicked()) {
                updateAppt();            
            } else {
                //saveAppt();
            }
            dialogStage.close();
        }
        
    }
    
    @FXML
    private void handleCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Cancel");
        alert.setHeaderText("Are you sure you want to Cancel?");
        alert.showAndWait()
        .filter(response -> response == ButtonType.OK)
        .ifPresent(response -> dialogStage.close());
    }
    
    /**
     * Initializes AppointmentEditScreen
     * @param dialogStage
     * @param currentUser 
     */
    
    public void setDialogStage(Stage dialogStage, user currentUser) {
        this.dialogStage = dialogStage;
        this.currentUser = currentUser;

        
/**
         * Sets time based on assumption of Business Hours being 8am - 5pm
         * So does not allow time selection outside of Business Hours
         */
	LocalTime time = LocalTime.of(8, 0);
	do {
		beginTimes.add(time.format(timeDTF));
		endTimes.add(time.format(timeDTF));
		time = time.plusMinutes(15);
	} while(!time.equals(LocalTime.of(17, 15)));
		beginTimes.remove(beginTimes.size() - 1);
		endTimes.remove(0);
        
        datePicker.setValue(LocalDate.now());

        beginComboBox.setItems(beginTimes);
	endComboBox.setItems(endTimes);
	beginComboBox.getSelectionModel().select(LocalTime.of(8, 0).format(timeDTF));
	endComboBox.getSelectionModel().select(LocalTime.of(8, 15).format(timeDTF));
        
    }
    
    /**
     * Sets appointment details from selected appointment in appointment Screen
     * into appointment Edit Screen
     * @param appointment 
     */
    
    public void setAppointment(appointment appointment) {
        
        okClicked = true;
        selectedAppt = appointment;
        
        LocalDateTime bt = appointment.getBegintime();
        LocalTime begintime = bt.toLocalTime();
        
        LocalDateTime et = appointment.getEndtime();
        LocalTime endtime = et.toLocalTime();
        
        apptLabel.setText("Edit Appointment");
        titleField.setText(appointment.getTitle());
        locationField.setText(appointment.getLocation());

        datePicker.setValue(appointment.getBegintime().toLocalDate());
    
        beginComboBox.getSelectionModel().select(bt.toLocalTime().format(timeDTF));
        endComboBox.getSelectionModel().select(et.toLocalTime().format(timeDTF));
        
        typeComboBox.setValue(appointment.getRemindertype());
        descriptionField.setText(appointment.getDescription());
    }
    
    /**
     * Inserts new appointment record
     */
    /*
    private void saveAppt() {
  
        LocalDate localDate = datePicker.getValue();
	LocalTime beginTime = LocalTime.parse(beginComboBox.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(endComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        
        LocalDateTime startDT = LocalDateTime.of(localDate, beginTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime startUTC = startDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));            
	
	Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime()); //this value can be inserted into database
        Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime()); //this value can be inserted into database        
        
        try {

                PreparedStatement pst = dbconnection.getConn().prepareStatement("INSERT INTO appointments "
                + "(customerId, title, description, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)"
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?, CURRENT_TIMESTAMP, ?)");
            
                //pst.setString(1, customerSelectTableView.getSelectionModel().getSelectedItem().getCustomerId());
                pst.setString(2, titleField.getText());
                pst.setString(3, typeComboBox.getValue());
                pst.setString(4, "");
                pst.setString(5, "");
                pst.setString(6, "");
                pst.setTimestamp(7, startsqlts);
                pst.setTimestamp(8, endsqlts);
                pst.setString(9, currentUser.getUsername());
                pst.setString(10, currentUser.getUsername());
                int result = pst.executeUpdate();
                if (result == 1) {//one row was affected; namely the one that was inserted!
                    System.out.println("YAY! New Appointment Save");
                } else {
                    System.out.println("BOO! New Appointment Save");
                }
            } catch (SQLException ex) {
            ex.printStackTrace();
            }

    }
    */
    /**
     * Updates edited appointment record in database
     */
    private void updateAppt() {
        
        LocalDate localDate = datePicker.getValue();
	LocalTime startTime = LocalTime.parse(beginComboBox.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(endComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        
        LocalDateTime startDT = LocalDateTime.of(localDate, startTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime startUTC = startDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));            
	
	Timestamp startsqlts = Timestamp.valueOf(startUTC.toLocalDateTime()); //this value can be inserted into database
        Timestamp endsqlts = Timestamp.valueOf(endUTC.toLocalDateTime()); //this value can be inserted into database        
        
        try {

                PreparedStatement pst = dbconnection.getConn().prepareStatement("UPDATE appointment "
                        + "SET customerId = ?, title = ?, description = ?, start = ?, end = ?, lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = ? "
                        + "WHERE appointmentId = ?");
            
                //pst.setString(1, customerSelectTableView.getSelectionModel().getSelectedItem().getCustomerId());
                pst.setString(2, titleField.getText());
                pst.setString(3, typeComboBox.getValue());
                pst.setTimestamp(4, startsqlts);
                pst.setTimestamp(5, endsqlts);
                pst.setString(6, currentUser.getUsername());
                pst.setInt(7, selectedAppt.getAppid());
                int result = pst.executeUpdate();
                if (result == 1) {//one row was affected; namely the one that was inserted!
                    System.out.println("YAY! Update Appointment Save");
                } else {
                    System.out.println("BOO! Update Appointment Save");
                }
            } catch (SQLException ex) {
            ex.printStackTrace();
            }
    }
    
    /**
     * Populates type list with predefined types
     */
    private void populateTypeList() {
        ObservableList<String> typeList = FXCollections.observableArrayList();
        typeList.addAll("phone", "email", "clock");
        typeComboBox.setItems(typeList);
    }
    
    /**
     * Validates appointment information before inserting or updating records
     * @return true if valid, false if there is an error in fields
     */
    
    
    
    private boolean validateAppointment() {
        String title = titleField.getText();
        String type = typeComboBox.getValue();
        String location = titleField.getText();
        LocalDate localDate = datePicker.getValue();
	LocalTime beginTime = LocalTime.parse(beginComboBox.getSelectionModel().getSelectedItem(), timeDTF);
	LocalTime endTime = LocalTime.parse(endComboBox.getSelectionModel().getSelectedItem(), timeDTF);
        
        LocalDateTime beginDT = LocalDateTime.of(localDate, beginTime);
        LocalDateTime endDT = LocalDateTime.of(localDate, endTime);

        ZonedDateTime beginUTC = beginDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));
        ZonedDateTime endUTC = endDT.atZone(zid).withZoneSameInstant(ZoneId.of("UTC"));            
	        
        String errorMessage = "";
        //first checks to see if inputs are null
        if (title == null || title.length() == 0) {
            errorMessage += "Please enter an Appointment title.\n"; 
        }
        
        if (type == null || type.length() == 0) {
            errorMessage += "Please select a reminder type.\n";  
        }
        
        if (location == null) {
            errorMessage += "Please Select a location.\n"; 
        } 
        
        if (beginUTC == null) {
            errorMessage += "Please select a Begin time"; 
        }         
        if (endUTC == null) {
            errorMessage += "Please select an End time.\n"; 
            //checks to make sure Start and End times are not the same
            } else if (endUTC.equals(beginUTC) || endUTC.isBefore(beginUTC)){
                errorMessage += "End time must be after Start time.\n";
            } else try {
                //checks user's existing appointments for time conflicts
                if (hasApptConflict(beginUTC, endUTC)){
                    errorMessage += "Appointment times conflict with Consultant's existing appointments. Please select a new time.\n";
                }
        } catch (SQLException ex) {
            Logger.getLogger(AppointmentEditScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid Appointment fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }
    
    
    
    /**
     * SELECT statement searches database against proposed times
     * @param newStart
     * @param newEnd
     * @return true if result set contains any matching appointments
     * @throws SQLException 
     */
    
    
    private boolean hasApptConflict(ZonedDateTime newStart, ZonedDateTime newEnd) throws SQLException {
        int apptID;
        String user;
        
        if (isOkClicked()) {
            //edited appointment
            apptID = selectedAppt.getAppid();
        } else {
            //new appointment
            user = currentUser.getUsername();
        }
        //System.out.println("ApptID: " + apptID);
        
        try {
            
        PreparedStatement pst = dbconnection.getConn().prepareStatement(
            "SELECT * FROM appointment "
	+   "WHERE (? BETWEEN begin AND end OR ? BETWEEN begin AND end OR ? < begin AND ? > end) "
	+   "AND appointmentID != ?");
        pst.setTimestamp(1, Timestamp.valueOf(newStart.toLocalDateTime()));
	pst.setTimestamp(2, Timestamp.valueOf(newEnd.toLocalDateTime()));
        pst.setTimestamp(3, Timestamp.valueOf(newStart.toLocalDateTime()));
	pst.setTimestamp(4, Timestamp.valueOf(newEnd.toLocalDateTime()));
        //pst.setString(6, apptID);
        ResultSet rs = pst.executeQuery();
           
        if(rs.next()) {
            return true;
	}
            
        } catch (SQLException sqe) {
            System.out.println("Check your SQL");
            sqe.printStackTrace();
        } catch (Exception e) {
            System.out.println("Something besides the SQL went wrong.");
            e.printStackTrace();
        }
        return false;
    }
    
}
