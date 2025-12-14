package org.example.vetclinic.model;
public class Breed {
    private Integer id;
    private Integer speciesId;
    private String breedName;
    private String speciesName;
    public Breed(Integer id, Integer speciesId, String breedName) {
        this.id = id;
        this.speciesId = speciesId;
        this.breedName = breedName;
    }
    public Breed(Integer id, Integer speciesId, String breedName, String speciesName) {
        this.id = id;
        this.speciesId = speciesId;
        this.breedName = breedName;
        this.speciesName = speciesName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getSpeciesId() {
        return speciesId;
    }
    public void setSpeciesId(Integer speciesId) {
        this.speciesId = speciesId;
    }
    public String getBreedName() {
        return breedName;
    }
    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }
    public String getSpeciesName() {
        return speciesName;
    }
    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }
    @Override
    public String toString() {
        return breedName + (speciesName != null ? " (" + speciesName + ")" : "");
    }
}
