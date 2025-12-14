package org.example.vetclinic.model;
import java.math.BigDecimal;
public class VisitsReport {
    private Integer employeeId;
    private String employeeName;
    private Integer clientId;
    private String clientName;
    private Integer visitCount;
    private BigDecimal totalCost;
    private String averageVisitDuration;
    private Integer totalVisits;
    private BigDecimal totalCostSum;
    private Integer uniqueClients;
    private String averageVisitDurationSummary;
    public VisitsReport(Integer employeeId, String employeeName, Integer clientId, String clientName, 
                       Integer visitCount, BigDecimal totalCost) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.clientId = clientId;
        this.clientName = clientName;
        this.visitCount = visitCount;
        this.totalCost = totalCost;
    }
    public Integer getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    public String getEmployeeName() {
        return employeeName;
    }
    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
    public Integer getClientId() {
        return clientId;
    }
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public Integer getVisitCount() {
        return visitCount;
    }
    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    public Integer getTotalVisits() {
        return totalVisits;
    }
    public void setTotalVisits(Integer totalVisits) {
        this.totalVisits = totalVisits;
    }
    public BigDecimal getTotalCostSum() {
        return totalCostSum;
    }
    public void setTotalCostSum(BigDecimal totalCostSum) {
        this.totalCostSum = totalCostSum;
    }
    public Integer getUniqueClients() {
        return uniqueClients;
    }
    public void setUniqueClients(Integer uniqueClients) {
        this.uniqueClients = uniqueClients;
    }
    public String getAverageVisitDuration() {
        return averageVisitDuration;
    }
    public void setAverageVisitDuration(String averageVisitDuration) {
        this.averageVisitDuration = averageVisitDuration;
    }
    public String getAverageVisitDurationSummary() {
        return averageVisitDurationSummary;
    }
    public void setAverageVisitDurationSummary(String averageVisitDurationSummary) {
        this.averageVisitDurationSummary = averageVisitDurationSummary;
    }
}
