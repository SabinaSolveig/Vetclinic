package org.example.vetclinic.model;
public class ServiceCategory {
    private Integer id;
    private String categoryName;
    public ServiceCategory(Integer id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    @Override
    public String toString() {
        return categoryName;
    }
}
