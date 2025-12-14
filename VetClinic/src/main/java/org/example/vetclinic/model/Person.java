package org.example.vetclinic.model;
abstract class Person {
    protected Integer id;
    protected String firstName;
    private String lastName;
    private String middleName;
    public Person(Integer id, String firstName, String lastName, String middleName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getMiddleName() {
        return middleName;
    }
}
