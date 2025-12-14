package org.example.vetclinic.model;
import org.example.vetclinic.util.StatusTranslator;
import java.time.LocalDate;
import java.time.LocalTime;
public class Appointment {
    private Integer id;
    private Integer clientId;
    private Integer petId;
    private Integer employeeId;
    private LocalDate appointmentDate;
    private LocalTime appointmentTime;
    private String status;
    private String notes;
    private String clientName;
    private String petName;
    private String employeeName;
    public Appointment(Integer id, Integer clientId, Integer petId, Integer employeeId,
                       LocalDate appointmentDate, LocalTime appointmentTime, String status, String notes) {
        this.id = id;
        this.clientId = clientId;
        this.petId = petId;
        this.employeeId = employeeId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
    }
    public Appointment(Integer id, Integer clientId, Integer petId, Integer employeeId,
                       LocalDate appointmentDate, LocalTime appointmentTime, String status, String notes,
                       String clientName, String petName, String employeeName) {
        this.id = id;
        this.clientId = clientId;
        this.petId = petId;
        this.employeeId = employeeId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
        this.clientName = clientName;
        this.petName = petName;
        this.employeeName = employeeName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getClientId() {
        return clientId;
    }
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
    public Integer getPetId() {
        return petId;
    }
    public void setPetId(Integer petId) {
        this.petId = petId;
    }
    public Integer getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }
    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    public LocalTime getAppointmentTime() {
        return appointmentTime;
    }
    public void setAppointmentTime(LocalTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getPetName() {
        return petName;
    }
    public void setPetName(String petName) {
        this.petName = petName;
    }
    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    public String getStatusDisplay() {
        return StatusTranslator.translateAppointmentStatus(status);
    }
    @Override
    public String toString() {
        return "Заявка #" + id;
    }
}
