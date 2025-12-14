package org.example.vetclinic.model;
import java.math.BigDecimal;
public class Service {
    private Integer id;
    private String serviceName;
    private Integer serviceCategoryId;
    private BigDecimal price;
    private String description;
    private String categoryName;
    public Service(Integer id, String serviceName, Integer serviceCategoryId, BigDecimal price, String description) {
        this.id = id;
        this.serviceName = serviceName;
        this.serviceCategoryId = serviceCategoryId;
        this.price = price;
        this.description = description;
    }
    public Service(Integer id, String serviceName, Integer serviceCategoryId, BigDecimal price, String description, String categoryName) {
        this.id = id;
        this.serviceName = serviceName;
        this.serviceCategoryId = serviceCategoryId;
        this.price = price;
        this.description = description;
        this.categoryName = categoryName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getServiceName() {
        return serviceName;
    }
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    public Integer getServiceCategoryId() {
        return serviceCategoryId;
    }
    public void setServiceCategoryId(Integer serviceCategoryId) {
        this.serviceCategoryId = serviceCategoryId;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    @Override
    public String toString() {
        return serviceName;
    }
}
