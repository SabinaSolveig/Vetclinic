package org.example.vetclinic.model;
import java.math.BigDecimal;
public class VisitProduct {
    private Integer visitId;
    private Integer productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal sum;
    private String productName;
    public VisitProduct(Integer visitId, Integer productId, Integer quantity, BigDecimal price, BigDecimal sum) {
        this.visitId = visitId;
        this.productId = productId;
        this.quantity = quantity != null ? quantity : 1;
        this.price = price;
        this.sum = sum;
    }
    public VisitProduct(Integer visitId, Integer productId, Integer quantity, BigDecimal price, BigDecimal sum, String productName) {
        this.visitId = visitId;
        this.productId = productId;
        this.quantity = quantity != null ? quantity : 1;
        this.price = price;
        this.sum = sum;
        this.productName = productName;
    }
    public Integer getVisitId() {
        return visitId;
    }
    public void setVisitId(Integer visitId) {
        this.visitId = visitId;
    }
    public Integer getProductId() {
        return productId;
    }
    public void setProductId(Integer productId) {
        this.productId = productId;
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
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    @Override
    public String toString() {
        return productName != null ? productName : "Товар #" + productId;
    }
}
