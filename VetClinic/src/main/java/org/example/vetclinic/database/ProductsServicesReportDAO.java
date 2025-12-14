package org.example.vetclinic.database;
import org.example.vetclinic.model.ProductsServicesReport;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class ProductsServicesReportDAO {
    public List<ProductsServicesReport> getProductsServicesReport(LocalDate startDate, LocalDate endDate,
                                                                   Integer employeeId, String itemType,
                                                                   String sortField, boolean ascending) {
        List<ProductsServicesReport> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    EmployeeID, " +
            "    EmployeeName, " +
            "    ItemType, " +
            "    ItemName, " +
            "    SUM(Quantity) AS Quantity, " +
            "    SUM(Cost) AS Cost " +
            "FROM products_services_report_view " +
            "WHERE VisitDate >= ? AND VisitDate <= ?"
        );
        List<Object> parameters = new ArrayList<>();
        parameters.add(Date.valueOf(startDate));
        parameters.add(Date.valueOf(endDate));
        if (employeeId != null) {
            sql.append(" AND EmployeeID = ?");
            parameters.add(employeeId);
        }
        if (itemType != null && !itemType.isEmpty() && !itemType.equals("Все")) {
            sql.append(" AND ItemType = ?");
            parameters.add(itemType);
        }
        sql.append(" GROUP BY EmployeeID, EmployeeName, ItemType, ItemID, ItemName");
        sql.append(" ORDER BY ");
        String actualSortField = "EmployeeName";
        switch (sortField) {
            case "Врач":
                actualSortField = "EmployeeName";
                break;
            case "Тип":
                actualSortField = "ItemType";
                break;
            case "Товар/Услуга":
                actualSortField = "ItemName";
                break;
            case "Количество":
                actualSortField = "Quantity";
                break;
            case "Стоимость":
                actualSortField = "Cost";
                break;
        }
        sql.append(actualSortField).append(ascending ? " ASC" : " DESC");
        if (!actualSortField.equals("EmployeeName")) {
            sql.append(", EmployeeName ASC");
        }
        if (!actualSortField.equals("ItemType") && !actualSortField.equals("EmployeeName")) {
            sql.append(", ItemType ASC");
        }
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                }
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer empId = rs.getInt("EmployeeID");
                String empName = rs.getString("EmployeeName");
                String type = rs.getString("ItemType");
                String name = rs.getString("ItemName");
                Integer qty = rs.getInt("Quantity");
                BigDecimal cost = rs.getBigDecimal("Cost");
                ProductsServicesReport report = new ProductsServicesReport(empId, empName, type, name, qty, cost);
                list.add(report);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке отчета по товарам и услугам");
        }
        return list;
    }
    public List<ProductsServicesReport> getProductsServicesReportSummary(LocalDate startDate, LocalDate endDate,
                                                                         Integer employeeId, String itemType) {
        List<ProductsServicesReport> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    EmployeeID, " +
            "    EmployeeName, " +
            "    ItemType, " +
            "    SUM(Quantity) AS TotalQuantity, " +
            "    SUM(Cost) AS TotalCost " +
            "FROM products_services_report_view " +
            "WHERE VisitDate >= ? AND VisitDate <= ?"
        );
        List<Object> parameters = new ArrayList<>();
        parameters.add(Date.valueOf(startDate));
        parameters.add(Date.valueOf(endDate));
        if (employeeId != null) {
            sql.append(" AND EmployeeID = ?");
            parameters.add(employeeId);
        }
        if (itemType != null && !itemType.isEmpty() && !itemType.equals("Все")) {
            sql.append(" AND ItemType = ?");
            parameters.add(itemType);
        }
        sql.append(" GROUP BY EmployeeID, EmployeeName, ItemType");
        sql.append(" ORDER BY EmployeeName, ItemType");
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                } else if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                }
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer empId = rs.getInt("EmployeeID");
                String empName = rs.getString("EmployeeName");
                String type = rs.getString("ItemType");
                Integer totalQty = rs.getInt("TotalQuantity");
                BigDecimal totalCost = rs.getBigDecimal("TotalCost");
                ProductsServicesReport report = new ProductsServicesReport(empId, empName, type, null, null, null);
                report.setTotalQuantity(totalQty);
                report.setTotalCost(totalCost);
                list.add(report);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке итоговых данных отчета");
        }
        return list;
    }
}
