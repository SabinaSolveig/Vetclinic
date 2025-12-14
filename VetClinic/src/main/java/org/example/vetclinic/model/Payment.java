package org.example.vetclinic.model;
import org.example.vetclinic.util.StatusTranslator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public class Payment {
    private Integer id;
    private Integer visitId;
    private LocalDateTime paymentDate;
    private BigDecimal amount;
    private Integer paymentMethodId;
    private String status;
    private String notes;
    private String visitInfo;
    private String paymentMethodName;
    public Payment(Integer id, Integer visitId, LocalDateTime paymentDate, BigDecimal amount, 
                   Integer paymentMethodId, String status, String notes) {
        this.id = id;
        this.visitId = visitId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethodId = paymentMethodId;
        this.status = status != null ? status : "Pending";
        this.notes = notes;
    }
    public Payment(Integer id, Integer visitId, LocalDateTime paymentDate, BigDecimal amount, 
                   Integer paymentMethodId, String status, String notes,
                   String visitInfo, String paymentMethodName) {
        this.id = id;
        this.visitId = visitId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.paymentMethodId = paymentMethodId;
        this.status = status != null ? status : "Pending";
        this.notes = notes;
        this.visitInfo = visitInfo;
        this.paymentMethodName = paymentMethodName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getVisitId() {
        return visitId;
    }
    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }
    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }
    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public Integer getPaymentMethodId() {
        return paymentMethodId;
    }
    public void setPaymentMethodId(Integer paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status != null ? status : "Pending";
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getVisitInfo() {
        return visitInfo;
    }
    public void setVisitInfo(String visitInfo) {
        this.visitInfo = visitInfo;
    }
    public String getPaymentMethodName() {
        return paymentMethodName;
    }
    public void setPaymentMethodName(String paymentMethodName) {
        this.paymentMethodName = paymentMethodName;
    }
    public String getStatusDisplay() {
        return StatusTranslator.translatePaymentStatus(status);
    }
    @Override
    public String toString() {
        return "Оплата #" + id;
    }
}
