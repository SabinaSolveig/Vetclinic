package org.example.vetclinic.model;
public class Species {
    private Integer id;
    private String speciesName;
    public Species(Integer id, String speciesName) {
        this.id = id;
        this.speciesName = speciesName;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getSpeciesName() {
        return speciesName;
    }
    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }
    @Override
    public String toString() {
        return speciesName;
    }
}
