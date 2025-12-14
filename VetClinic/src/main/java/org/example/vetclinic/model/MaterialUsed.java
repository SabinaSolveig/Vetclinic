package org.example.vetclinic.model;
import java.math.BigDecimal;
public class MaterialUsed {
    private Integer visitId;
    private Integer productId;
    private BigDecimal quantity;
    private String productName;
    public MaterialUsed(Integer visitId, Integer productId, BigDecimal quantity) {
        this.visitId = visitId;
        this.productId = productId;
        this.quantity = quantity;
    }
    public MaterialUsed(Integer visitId, Integer productId, BigDecimal quantity, String productName) {
        this.visitId = visitId;
        this.productId = productId;
        this.quantity = quantity;
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
    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    @Override
    public String toString() {
        return productName != null ? productName + " (" + quantity + ")" : "Товар #" + productId + " (" + quantity + ")";
    }
}
