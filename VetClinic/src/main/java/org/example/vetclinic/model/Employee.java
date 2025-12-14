package org.example.vetclinic.model;
import java.time.LocalDate;
public class Employee extends Person {
    private LocalDate birthDate;
    private Integer specializationId;
    private LocalDate hireDate;
    private LocalDate dismissalDate;
    private Boolean active;
    private String specializationName;
    public Employee(Integer id, String firstName, String lastName, String middleName) {
        super(id, firstName, lastName, middleName);
    }
    public Employee(Integer id, String firstName, String lastName, String middleName,
                     LocalDate birthDate, Integer specializationId, LocalDate hireDate,
                     LocalDate dismissalDate, Boolean active) {
        super(id, firstName, lastName, middleName);
        this.birthDate = birthDate;
        this.specializationId = specializationId;
        this.hireDate = hireDate;
        this.dismissalDate = dismissalDate;
        this.active = active;
    }
    public Employee(Integer id, String firstName, String lastName, String middleName,
                     LocalDate birthDate, Integer specializationId, LocalDate hireDate,
                     LocalDate dismissalDate, Boolean active, String specializationName) {
        super(id, firstName, lastName, middleName);
        this.birthDate = birthDate;
        this.specializationId = specializationId;
        this.hireDate = hireDate;
        this.dismissalDate = dismissalDate;
        this.active = active;
        this.specializationName = specializationName;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    public Integer getSpecializationId() {
        return specializationId;
    }
    public void setSpecializationId(Integer specializationId) {
        this.specializationId = specializationId;
    }
    public LocalDate getHireDate() {
        return hireDate;
    }
    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
    public LocalDate getDismissalDate() {
        return dismissalDate;
    }
    public void setDismissalDate(LocalDate dismissalDate) {
        this.dismissalDate = dismissalDate;
    }
    public Boolean getActive() {
        return active;
    }
    public void setActive(Boolean active) {
        this.active = active;
    }
    public String getSpecializationName() {
        return specializationName;
    }
    public void setSpecializationName(String specializationName) {
        this.specializationName = specializationName;
    }
    public Integer getId() {
        return id;
    }
}
