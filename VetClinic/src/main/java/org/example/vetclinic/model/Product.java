package org.example.vetclinic.model;
import java.math.BigDecimal;
public class Product {
    private Integer id;
    private String productName;
    private Integer productCategoryId;
    private BigDecimal price;
    private Integer stockQuantity;
    private String categoryName;
    public Product(Integer id, String productName, Integer productCategoryId, BigDecimal price, Integer stockQuantity) {
        this.id = id;
        this.productName = productName;
        this.productCategoryId = productCategoryId;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
    public Product(Integer id, String productName, Integer productCategoryId, BigDecimal price, Integer stockQuantity, String categoryName) {
        this.id = id;
        this.productName = productName;
        this.productCategoryId = productCategoryId;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryName = categoryName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public Integer getProductCategoryId() {
        return productCategoryId;
    }
    public void setProductCategoryId(Integer productCategoryId) {
        this.productCategoryId = productCategoryId;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    @Override
    public String toString() {
        return productName;
    }
}
