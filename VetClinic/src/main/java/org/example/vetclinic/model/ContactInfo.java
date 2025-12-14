package org.example.vetclinic.model;
public class ContactInfo {
    private Integer id;
    private String ownerType;
    private Integer ownerId;
    private Integer contactTypeId;
    private String contactValue;
    private Boolean isPrimary;
    private String notes;
    private String contactTypeName;
    public ContactInfo(Integer id, String ownerType, Integer ownerId, Integer contactTypeId,
                       String contactValue, Boolean isPrimary, String notes) {
        this.id = id;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.contactTypeId = contactTypeId;
        this.contactValue = contactValue;
        this.isPrimary = isPrimary;
        this.notes = notes;
    }
    public ContactInfo(Integer id, String ownerType, Integer ownerId, Integer contactTypeId,
                       String contactValue, Boolean isPrimary, String notes, String contactTypeName) {
        this.id = id;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.contactTypeId = contactTypeId;
        this.contactValue = contactValue;
        this.isPrimary = isPrimary;
        this.notes = notes;
        this.contactTypeName = contactTypeName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getOwnerType() {
        return ownerType;
    }
    public void setOwnerType(String ownerType) {
        this.ownerType = ownerType;
    }
    public Integer getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }
    public Integer getContactTypeId() {
        return contactTypeId;
    }
    public void setContactTypeId(Integer contactTypeId) {
        this.contactTypeId = contactTypeId;
    }
    public String getContactValue() {
        return contactValue;
    }
    public void setContactValue(String contactValue) {
        this.contactValue = contactValue;
    }
    public Boolean getIsPrimary() {
        return isPrimary;
    }
    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getContactTypeName() {
        return contactTypeName;
    }
    public void setContactTypeName(String contactTypeName) {
        this.contactTypeName = contactTypeName;
    }
    @Override
    public String toString() {
        return contactValue;
    }
}
