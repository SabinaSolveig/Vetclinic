package org.example.vetclinic.model;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
public class Visit {
    private Integer id;
    private Integer appointmentId;
    private Integer clientId;
    private Integer petId;
    private Integer employeeId;
    private LocalDate visitDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String diagnosis;
    private String anamnesis;
    private String treatment;
    private String recommendations;
    private BigDecimal totalCost;
    private String clientName;
    private String petName;
    private String employeeName;
    private String paymentStatus;
    public Visit(Integer id, Integer appointmentId, Integer clientId, Integer petId, Integer employeeId,
                 LocalDate visitDate, LocalTime startTime, LocalTime endTime, String diagnosis, String anamnesis,
                 String treatment, String recommendations, BigDecimal totalCost) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.clientId = clientId;
        this.petId = petId;
        this.employeeId = employeeId;
        this.visitDate = visitDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.diagnosis = diagnosis;
        this.anamnesis = anamnesis;
        this.treatment = treatment;
        this.recommendations = recommendations;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
    }
    public Visit(Integer id, Integer appointmentId, Integer clientId, Integer petId, Integer employeeId,
                 LocalDate visitDate, LocalTime startTime, LocalTime endTime, String diagnosis, String anamnesis,
                 String treatment, String recommendations, BigDecimal totalCost,
                 String clientName, String petName, String employeeName) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.clientId = clientId;
        this.petId = petId;
        this.employeeId = employeeId;
        this.visitDate = visitDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.diagnosis = diagnosis;
        this.anamnesis = anamnesis;
        this.treatment = treatment;
        this.recommendations = recommendations;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
        this.clientName = clientName;
        this.petName = petName;
        this.employeeName = employeeName;
        this.paymentStatus = "Не оплачен";
    }
    public Visit(Integer id, Integer appointmentId, Integer clientId, Integer petId, Integer employeeId,
                 LocalDate visitDate, LocalTime startTime, LocalTime endTime, String diagnosis, String anamnesis,
                 String treatment, String recommendations, BigDecimal totalCost,
                 String clientName, String petName, String employeeName, String paymentStatus) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.clientId = clientId;
        this.petId = petId;
        this.employeeId = employeeId;
        this.visitDate = visitDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.diagnosis = diagnosis;
        this.anamnesis = anamnesis;
        this.treatment = treatment;
        this.recommendations = recommendations;
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
        this.clientName = clientName;
        this.petName = petName;
        this.employeeName = employeeName;
        this.paymentStatus = paymentStatus != null ? paymentStatus : "Не оплачен";
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(Integer appointmentId) {
        this.appointmentId = appointmentId;
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
    public LocalDate getVisitDate() {
        return visitDate;
    }
    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }
    public LocalTime getStartTime() {
        return startTime;
    }
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }
    public LocalTime getEndTime() {
        return endTime;
    }
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }
    public String getDiagnosis() {
        return diagnosis;
    }
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }
    public String getAnamnesis() {
        return anamnesis;
    }
    public void setAnamnesis(String anamnesis) {
        this.anamnesis = anamnesis;
    }
    public String getTreatment() {
        return treatment;
    }
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }
    public String getRecommendations() {
        return recommendations;
    }
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
    }
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost != null ? totalCost : BigDecimal.ZERO;
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
    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus != null ? paymentStatus : "Не оплачен";
    }
    @Override
    public String toString() {
        return "Прием #" + id;
    }
}
