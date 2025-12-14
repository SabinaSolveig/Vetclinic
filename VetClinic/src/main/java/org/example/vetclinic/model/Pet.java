package org.example.vetclinic.model;
import java.time.LocalDate;
public class Pet {
    private Integer id;
    private Integer clientId;
    private String name;
    private Integer speciesId;
    private Integer breedId;
    private LocalDate birthDate;
    private Integer age;
    private String gender;
    private String notes;
    private String clientName;
    private String speciesName;
    private String breedName;
    public Pet(Integer id, Integer clientId, String name, Integer speciesId, Integer breedId,
                LocalDate birthDate, Integer age, String gender, String notes) {
        this.id = id;
        this.clientId = clientId;
        this.name = name;
        this.speciesId = speciesId;
        this.breedId = breedId;
        this.birthDate = birthDate;
        this.age = age;
        this.gender = gender;
        this.notes = notes;
    }
    public Pet(Integer id, Integer clientId, String name, Integer speciesId, Integer breedId,
                LocalDate birthDate, Integer age, String gender, String notes,
                String clientName, String speciesName, String breedName) {
        this.id = id;
        this.clientId = clientId;
        this.name = name;
        this.speciesId = speciesId;
        this.breedId = breedId;
        this.birthDate = birthDate;
        this.age = age;
        this.gender = gender;
        this.notes = notes;
        this.clientName = clientName;
        this.speciesName = speciesName;
        this.breedName = breedName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getClientId() {
        return clientId;
    }
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getSpeciesId() {
        return speciesId;
    }
    public void setSpeciesId(Integer speciesId) {
        this.speciesId = speciesId;
    }
    public Integer getBreedId() {
        return breedId;
    }
    public void setBreedId(Integer breedId) {
        this.breedId = breedId;
    }
    public LocalDate getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public String getGender() {
        return gender;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public String getSpeciesName() {
        return speciesName;
    }
    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }
    public String getBreedName() {
        return breedName;
    }
    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }
    @Override
    public String toString() {
        return name;
    }
}
