package org.example.vetclinic.model;
public class PreliminaryServiceSet {
    private Integer id;
    private Integer appointmentId;
    private Integer serviceId;
    private Integer quantity;
    private String notes;
    private String serviceName;
    private java.math.BigDecimal servicePrice;
    public PreliminaryServiceSet(Integer id, Integer appointmentId, Integer serviceId, Integer quantity, String notes) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.serviceId = serviceId;
        this.quantity = quantity != null ? quantity : 1;
        this.notes = notes;
    }
    public PreliminaryServiceSet(Integer id, Integer appointmentId, Integer serviceId, Integer quantity, String notes,
                                  String serviceName, java.math.BigDecimal servicePrice) {
        this.id = id;
        this.appointmentId = appointmentId;
        this.serviceId = serviceId;
        this.quantity = quantity != null ? quantity : 1;
        this.notes = notes;
        this.serviceName = serviceName;
        this.servicePrice = servicePrice;
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
    public Integer getServiceId() {
        return serviceId;
    }
    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity != null ? quantity : 1;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public java.math.BigDecimal getServicePrice() {
        return servicePrice;
    }
    public void setServicePrice(java.math.BigDecimal servicePrice) {
        this.servicePrice = servicePrice;
    }
    public java.math.BigDecimal getTotalPrice() {
        if (servicePrice != null && quantity != null) {
            return servicePrice.multiply(new java.math.BigDecimal(quantity));
        }
        return java.math.BigDecimal.ZERO;
    }
    @Override
    public String toString() {
        return serviceName != null ? serviceName : "Услуга #" + serviceId;
    }
}
