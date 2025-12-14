package org.example.vetclinic.model;
import java.math.BigDecimal;
public class ProductsServicesReport {
    private Integer employeeId;
    private String employeeName;
    private String itemType;
    private String itemName;
    private Integer quantity;
    private BigDecimal cost;
    private Integer totalQuantity;
    private BigDecimal totalCost;
    public ProductsServicesReport(Integer employeeId, String employeeName, String itemType, 
                                 String itemName, Integer quantity, BigDecimal cost) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.itemType = itemType;
        this.itemName = itemName;
        this.quantity = quantity;
        this.cost = cost;
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
    public String getItemType() {
        return itemType;
    }
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
    public String getItemName() {
        return itemName;
    }
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public BigDecimal getCost() {
        return cost;
    }
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    public Integer getTotalQuantity() {
        return totalQuantity;
    }
    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
}
