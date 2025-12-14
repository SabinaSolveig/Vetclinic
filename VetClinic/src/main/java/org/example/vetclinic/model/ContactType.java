package org.example.vetclinic.model;
public class ContactType {
    private Integer id;
    private String contactTypeName;
    private String description;
    public ContactType(Integer id, String contactTypeName, String description) {
        this.id = id;
        this.contactTypeName = contactTypeName;
        this.description = description;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getContactTypeName() {
        return contactTypeName;
    }
    public void setContactTypeName(String contactTypeName) {
        this.contactTypeName = contactTypeName;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    @Override
    public String toString() {
        return contactTypeName;
    }
}
