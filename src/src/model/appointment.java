
package model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class appointment {
    
    private int appointmentId;
    private int userId;
    private String title;
    private String description;
    private String location;
    private LocalDateTime begintime;
    private LocalDateTime endTime;
    private String reminderType;
    
    
    public appointment() {
    }

    public appointment(int appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    
    public appointment(int appointmentId, String title, String location, String description, LocalDateTime beginTime, LocalDateTime endTime) {
        this.title = title;
        this.location = location;
        this.appointmentId = appointmentId;
        this.description = description;
        this.begintime = beginTime;
        this.endTime = endTime;
    }
    
    public int getAppid() {
        return appointmentId;
    }

    public void setAppid(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getRemindertype() {
        return reminderType;
    }

    public void setRemindertype(String reminderType) {
        this.reminderType = reminderType;
    }
    public LocalDateTime getBegintime() {
        return begintime;
    }

    public void setBegintime(LocalDateTime begintime) {
        this.begintime = begintime;
    }

    public LocalDateTime getEndtime() {
        return endTime;
    }

    public void setEndtime(LocalDateTime end) {
        this.endTime = endTime;
    }
    
    //toString Override only used in troubleshooting during development
    /*
    @Override
    public String toString() {
        return "ID: " + this.appointmentId +
                " Start: " + this.start +
                " End: " + this.end +
                " Title: " + this.title +
                " Type: " + this.description +
                " Customer: " + this.customer.getCustomerName() +
                " Consultant: " + this.user + ".\n" ;
    }
    */

    public int getUserID() {
        return userId;
    }

    public void setUserId(String user) {
        this.userId = userId;
    }
    
    public void formatDate(String date) {
        
    }
}
