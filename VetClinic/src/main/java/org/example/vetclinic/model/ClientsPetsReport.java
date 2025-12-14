package org.example.vetclinic.model;
import java.math.BigDecimal;
public class ClientsPetsReport {
    private Integer clientId;
    private String clientName;
    private Integer petId;
    private String petName;
    private String speciesName;
    private String breedName;
    private Integer visitCount;
    private BigDecimal totalCost;
    private BigDecimal averageCheck;
    private Integer servicesCount;
    private Integer productsCount;
    private BigDecimal paidVisitsPercent;
    private Integer totalClients;
    private Integer totalPets;
    private Integer totalVisits;
    private BigDecimal totalCostSum;
    private BigDecimal averageCheckOverall;
    private BigDecimal averageCheckPerPet;
    public ClientsPetsReport(Integer clientId, String clientName, Integer petId, String petName,
                            String speciesName, String breedName, Integer visitCount, 
                            BigDecimal totalCost, BigDecimal averageCheck, Integer servicesCount,
                            Integer productsCount, BigDecimal paidVisitsPercent) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.petId = petId;
        this.petName = petName;
        this.speciesName = speciesName;
        this.breedName = breedName;
        this.visitCount = visitCount;
        this.totalCost = totalCost;
        this.averageCheck = averageCheck;
        this.servicesCount = servicesCount;
        this.productsCount = productsCount;
        this.paidVisitsPercent = paidVisitsPercent;
    }
    public Integer getClientId() {
        return clientId;
    }
    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }
    public String getClientName() {
        return clientName;
    }
    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    public Integer getPetId() {
        return petId;
    }
    public void setPetId(Integer petId) {
        this.petId = petId;
    }
    public String getPetName() {
        return petName;
    }
    public void setPetName(String petName) {
        this.petName = petName;
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
    public Integer getVisitCount() {
        return visitCount;
    }
    public void setVisitCount(Integer visitCount) {
        this.visitCount = visitCount;
    }
    public BigDecimal getTotalCost() {
        return totalCost;
    }
    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }
    public BigDecimal getAverageCheck() {
        return averageCheck;
    }
    public void setAverageCheck(BigDecimal averageCheck) {
        this.averageCheck = averageCheck;
    }
    public Integer getServicesCount() {
        return servicesCount;
    }
    public void setServicesCount(Integer servicesCount) {
        this.servicesCount = servicesCount;
    }
    public Integer getProductsCount() {
        return productsCount;
    }
    public void setProductsCount(Integer productsCount) {
        this.productsCount = productsCount;
    }
    public BigDecimal getPaidVisitsPercent() {
        return paidVisitsPercent;
    }
    public void setPaidVisitsPercent(BigDecimal paidVisitsPercent) {
        this.paidVisitsPercent = paidVisitsPercent;
    }
    public Integer getTotalClients() {
        return totalClients;
    }
    public void setTotalClients(Integer totalClients) {
        this.totalClients = totalClients;
    }
    public Integer getTotalPets() {
        return totalPets;
    }
    public void setTotalPets(Integer totalPets) {
        this.totalPets = totalPets;
    }
    public Integer getTotalVisits() {
        return totalVisits;
    }
    public void setTotalVisits(Integer totalVisits) {
        this.totalVisits = totalVisits;
    }
    public BigDecimal getTotalCostSum() {
        return totalCostSum;
    }
    public void setTotalCostSum(BigDecimal totalCostSum) {
        this.totalCostSum = totalCostSum;
    }
    public BigDecimal getAverageCheckOverall() {
        return averageCheckOverall;
    }
    public void setAverageCheckOverall(BigDecimal averageCheckOverall) {
        this.averageCheckOverall = averageCheckOverall;
    }
    public BigDecimal getAverageCheckPerPet() {
        return averageCheckPerPet;
    }
    public void setAverageCheckPerPet(BigDecimal averageCheckPerPet) {
        this.averageCheckPerPet = averageCheckPerPet;
    }
}
