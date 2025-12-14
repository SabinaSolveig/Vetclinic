package org.example.vetclinic.model;
public class Client extends Person {
    private String adress;
    private Integer discountpercent;
    private String notes;
    public Client(Integer id, String firstName, String lastName, String middleName) {
        super(id, firstName, lastName, middleName);
    }
    public Client(Integer id, String firstName, String lastName, String middleName, 
                   String address, Integer discountPercent, String notes) {
        super(id, firstName, lastName, middleName);
        this.adress = address;
        this.discountpercent = discountPercent;
        this.notes = notes;
    }
    public String getAddress() {
        return adress;
    }
    public Integer getDiscountPercent() {
        return discountpercent;
    }
    public String getNotes() {
        return notes;
    }
    public Integer getId() {
        return id;
    }
}
