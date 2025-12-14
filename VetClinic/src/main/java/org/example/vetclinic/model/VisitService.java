package org.example.vetclinic.model;
import java.math.BigDecimal;
public class VisitService {
    private Integer visitId;
    private Integer serviceId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal sum;
    private BigDecimal discountSum;
    private BigDecimal sumWithDiscount;
    private String serviceName;
    public VisitService(Integer visitId, Integer serviceId, Integer quantity, BigDecimal price, 
                       BigDecimal sum, BigDecimal discountSum, BigDecimal sumWithDiscount) {
        this.visitId = visitId;
        this.serviceId = serviceId;
        this.quantity = quantity != null ? quantity : 1;
        this.price = price;
        this.sum = sum;
        this.discountSum = discountSum != null ? discountSum : BigDecimal.ZERO;
        this.sumWithDiscount = sumWithDiscount;
    }
    public VisitService(Integer visitId, Integer serviceId, Integer quantity, BigDecimal price, 
                       BigDecimal sum, BigDecimal discountSum, BigDecimal sumWithDiscount, String serviceName) {
        this.visitId = visitId;
        this.serviceId = serviceId;
        this.quantity = quantity != null ? quantity : 1;
        this.price = price;
        this.sum = sum;
        this.discountSum = discountSum != null ? discountSum : BigDecimal.ZERO;
        this.sumWithDiscount = sumWithDiscount;
        this.serviceName = serviceName;
    }
    public Integer getVisitId() {
        return visitId;
    }
    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
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
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public BigDecimal getSum() {
        return sum;
    }
    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
    public BigDecimal getDiscountSum() {
        return discountSum;
    }
    public void setDiscountSum(BigDecimal discountSum) {
        this.discountSum = discountSum != null ? discountSum : BigDecimal.ZERO;
    }
    public BigDecimal getSumWithDiscount() {
        return sumWithDiscount;
    }
    public void setSumWithDiscount(BigDecimal sumWithDiscount) {
        this.sumWithDiscount = sumWithDiscount;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    @Override
    public String toString() {
        return serviceName != null ? serviceName : "Услуга #" + serviceId;
    }
}
