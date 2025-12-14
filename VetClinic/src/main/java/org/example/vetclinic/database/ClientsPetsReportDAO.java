package org.example.vetclinic.database;
import org.example.vetclinic.model.ClientsPetsReport;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class ClientsPetsReportDAO {
    public List<ClientsPetsReport> getClientsPetsReport(LocalDate startDate, LocalDate endDate,
                                                      Integer clientId, Integer speciesId, Integer breedId,
                                                      String sortField, boolean ascending) {
        List<ClientsPetsReport> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    ClientID, " +
            "    ClientName, " +
            "    PetID, " +
            "    PetName, " +
            "    SpeciesName, " +
            "    BreedName, " +
            "    SUM(VisitCount) AS VisitCount, " +
            "    SUM(TotalCost) AS TotalCost, " +
            "    CASE " +
            "        WHEN SUM(VisitCount) > 0 " +
            "        THEN ROUND(SUM(TotalCost) / SUM(VisitCount), 2) " +
            "        ELSE 0 " +
            "    END AS AverageCheck, " +
            "    SUM(ServicesCount) AS ServicesCount, " +
            "    SUM(ProductsCount) AS ProductsCount, " +
            "    CASE " +
            "        WHEN SUM(VisitCount) > 0 " +
            "        THEN ROUND(AVG(PaidVisitsPercent), 2) " +
            "        ELSE 0 " +
            "    END AS PaidVisitsPercent " +
            "FROM clients_pets_report_view " +
            "WHERE visitdate >= ? AND visitdate <= ?"
        );
        List<Object> parameters = new ArrayList<>();
        parameters.add(Date.valueOf(startDate));
        parameters.add(Date.valueOf(endDate));
        if (clientId != null) {
            sql.append(" AND ClientID = ?");
            parameters.add(clientId);
        }
        if (speciesId != null) {
            sql.append(" AND PetID IN (SELECT PetID FROM Pets WHERE SpeciesID = ?)");
            parameters.add(speciesId);
        }
        if (breedId != null) {
            sql.append(" AND PetID IN (SELECT PetID FROM Pets WHERE BreedID = ?)");
            parameters.add(breedId);
        }
        sql.append(" GROUP BY ClientID, ClientName, PetID, PetName, SpeciesName, BreedName");
        sql.append(" ORDER BY ");
        String actualSortField = "ClientName";
        if (sortField != null) {
            switch (sortField) {
                case "Клиент":
                    actualSortField = "ClientName";
                    break;
                case "Питомец":
                    actualSortField = "PetName";
                    break;
                case "Вид":
                    actualSortField = "SpeciesName";
                    break;
                case "Порода":
                    actualSortField = "BreedName";
                    break;
                case "Количество приемов":
                    actualSortField = "VisitCount";
                    break;
                case "Общая стоимость":
                    actualSortField = "TotalCost";
                    break;
                case "Средний чек":
                    actualSortField = "AverageCheck";
                    break;
                default:
                    actualSortField = "ClientName";
                    break;
            }
        }
        sql.append(actualSortField).append(ascending ? " ASC" : " DESC");
        if (!actualSortField.equals("ClientName")) {
            sql.append(", ClientName ASC");
        }
        if (!actualSortField.equals("PetName") && !actualSortField.equals("ClientName")) {
            sql.append(", PetName ASC");
        }
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                }
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer cId = rs.getInt("ClientID");
                String cName = rs.getString("ClientName");
                Integer pId = rs.getInt("PetID");
                String pName = rs.getString("PetName");
                String speciesName = rs.getString("SpeciesName");
                String breedName = rs.getString("BreedName");
                Integer visitCount = rs.getInt("VisitCount");
                BigDecimal totalCost = rs.getBigDecimal("TotalCost");
                BigDecimal avgCheck = rs.getBigDecimal("AverageCheck");
                Integer servicesCount = rs.getInt("ServicesCount");
                Integer productsCount = rs.getInt("ProductsCount");
                BigDecimal paidPercent = rs.getBigDecimal("PaidVisitsPercent");
                ClientsPetsReport report = new ClientsPetsReport(cId, cName, pId, pName, speciesName, breedName,
                                                                 visitCount, totalCost, avgCheck, servicesCount,
                                                                 productsCount, paidPercent);
                list.add(report);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке отчета по клиентам и питомцам");
        }
        return list;
    }
    public ClientsPetsReport getClientsPetsReportSummary(LocalDate startDate, LocalDate endDate,
                                                        Integer clientId, Integer speciesId, Integer breedId) {
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    COUNT(DISTINCT ClientID) AS TotalClients, " +
            "    COUNT(DISTINCT PetID) AS TotalPets, " +
            "    SUM(VisitCount) AS TotalVisits, " +
            "    SUM(TotalCost) AS TotalCostSum, " +
            "    CASE " +
            "        WHEN SUM(VisitCount) > 0 " +
            "        THEN ROUND(SUM(TotalCost) / SUM(VisitCount), 2) " +
            "        ELSE 0 " +
            "    END AS AverageCheckOverall, " +
            "    CASE " +
            "        WHEN COUNT(DISTINCT PetID) > 0 " +
            "        THEN ROUND(SUM(TotalCost) / COUNT(DISTINCT PetID), 2) " +
            "        ELSE 0 " +
            "    END AS AverageCheckPerPet " +
            "FROM clients_pets_report_view " +
            "WHERE visitdate >= ? AND visitdate <= ?"
        );
        List<Object> parameters = new ArrayList<>();
        parameters.add(Date.valueOf(startDate));
        parameters.add(Date.valueOf(endDate));
        if (clientId != null) {
            sql.append(" AND ClientID = ?");
            parameters.add(clientId);
        }
        if (speciesId != null) {
            sql.append(" AND PetID IN (SELECT PetID FROM Pets WHERE SpeciesID = ?)");
            parameters.add(speciesId);
        }
        if (breedId != null) {
            sql.append(" AND PetID IN (SELECT PetID FROM Pets WHERE BreedID = ?)");
            parameters.add(breedId);
        }
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                }
            }
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Integer totalClients = rs.getInt("TotalClients");
                Integer totalPets = rs.getInt("TotalPets");
                Integer totalVisits = rs.getInt("TotalVisits");
                BigDecimal totalCostSum = rs.getBigDecimal("TotalCostSum");
                BigDecimal avgCheckOverall = rs.getBigDecimal("AverageCheckOverall");
                BigDecimal avgCheckPerPet = rs.getBigDecimal("AverageCheckPerPet");
                ClientsPetsReport summary = new ClientsPetsReport(null, null, null, null, null, null,
                                                                  null, null, null, null, null, null);
                summary.setTotalClients(totalClients);
                summary.setTotalPets(totalPets);
                summary.setTotalVisits(totalVisits);
                summary.setTotalCostSum(totalCostSum);
                summary.setAverageCheckOverall(avgCheckOverall);
                summary.setAverageCheckPerPet(avgCheckPerPet);
                return summary;
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке итоговых данных отчета");
        }
        return null;
    }
}
