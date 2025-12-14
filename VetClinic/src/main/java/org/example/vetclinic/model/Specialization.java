package org.example.vetclinic.model;
public class Specialization {
    private Integer id;
    private String specializationName;
    private String description;
    public Specialization(Integer id, String specializationName, String description) {
        this.id = id;
        this.specializationName = specializationName;
        this.description = description;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getSpecializationName() {
        return specializationName;
    }
    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return specializationName;
    }
}
