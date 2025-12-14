package org.example.vetclinic.database;
import org.example.vetclinic.model.VisitsReport;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class VisitsReportDAO {
    public List<VisitsReport> getVisitsReport(LocalDate startDate, LocalDate endDate, 
                                             Integer employeeId, Integer clientId,
                                             String sortField, boolean ascending) {
        List<VisitsReport> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    e.EmployeeID, " +
            "    e.LastName || ' ' || e.FirstName || COALESCE(' ' || e.MiddleName, '') AS EmployeeName, " +
            "    c.ClientID, " +
            "    c.LastName || ' ' || c.FirstName || COALESCE(' ' || c.MiddleName, '') AS ClientName, " +
            "    COUNT(DISTINCT v.VisitID) AS VisitCount, " +
            "    COALESCE(SUM(v.TotalCost), 0) AS TotalCost, " +
            "    CASE " +
            "        WHEN COUNT(CASE WHEN v.StartTime IS NOT NULL AND v.EndTime IS NOT NULL THEN 1 END) > 0 " +
            "        THEN TO_CHAR(" +
            "            (DATE '2000-01-01' + AVG(EXTRACT(EPOCH FROM (v.EndTime - v.StartTime))) * INTERVAL '1 second'), " +
            "            'HH24:MI' " +
            "        ) " +
            "        ELSE NULL " +
            "    END AS AverageVisitDuration " +
            "FROM Visits v " +
            "INNER JOIN Employees e ON v.EmployeeID = e.EmployeeID " +
            "INNER JOIN Clients c ON v.ClientID = c.ClientID " +
            "WHERE v.VisitDate >= ? AND v.VisitDate <= ?"
        );
        List<Object> parameters = new ArrayList<>();
        parameters.add(Date.valueOf(startDate));
        parameters.add(Date.valueOf(endDate));
        if (employeeId != null) {
            sql.append(" AND v.EmployeeID = ?");
            parameters.add(employeeId);
        }
        if (clientId != null) {
            sql.append(" AND v.ClientID = ?");
            parameters.add(clientId);
        }
        sql.append(" GROUP BY " +
                  "    e.EmployeeID, " +
                  "    e.LastName, " +
                  "    e.FirstName, " +
                  "    e.MiddleName, " +
                  "    c.ClientID, " +
                  "    c.LastName, " +
                  "    c.FirstName, " +
                  "    c.MiddleName");
        if (sortField != null && !sortField.isEmpty()) {
            sql.append(" ORDER BY ").append(sortField);
            if (ascending) {
                sql.append(" ASC");
            } else {
                sql.append(" DESC");
            }
        } else {
            sql.append(" ORDER BY e.EmployeeID, c.ClientID");
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
                Integer empId = rs.getInt("EmployeeID");
                String empName = rs.getString("EmployeeName");
                Integer clId = rs.getInt("ClientID");
                String clName = rs.getString("ClientName");
                Integer visitCount = rs.getInt("VisitCount");
                BigDecimal totalCost = rs.getBigDecimal("TotalCost");
                String averageDuration = rs.getString("AverageVisitDuration");
                VisitsReport report = new VisitsReport(empId, empName, clId, clName, visitCount, totalCost);
                report.setAverageVisitDuration(averageDuration);
                list.add(report);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке отчета о приемах");
        }
        return list;
    }
    public List<VisitsReport> getVisitsReportSummary(LocalDate startDate, LocalDate endDate,
                                                     Integer employeeId, Integer clientId) {
        List<VisitsReport> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT " +
            "    e.EmployeeID, " +
            "    e.LastName || ' ' || e.FirstName || COALESCE(' ' || e.MiddleName, '') AS EmployeeName, " +
            "    COUNT(DISTINCT v.VisitID) AS TotalVisits, " +
            "    COALESCE(SUM(v.TotalCost), 0) AS TotalCostSum, " +
            "    COUNT(DISTINCT v.ClientID) AS UniqueClients, " +
            "    CASE " +
            "        WHEN COUNT(CASE WHEN v.StartTime IS NOT NULL AND v.EndTime IS NOT NULL THEN 1 END) > 0 " +
            "        THEN TO_CHAR(" +
            "            (DATE '2000-01-01' + AVG(EXTRACT(EPOCH FROM (v.EndTime - v.StartTime))) * INTERVAL '1 second'), " +
            "            'HH24:MI' " +
            "        ) " +
            "        ELSE NULL " +
            "    END AS AverageVisitDuration " +
            "FROM Visits v " +
            "INNER JOIN Employees e ON v.EmployeeID = e.EmployeeID " +
            "INNER JOIN Clients c ON v.ClientID = c.ClientID " +
            "WHERE v.VisitDate >= ? AND v.VisitDate <= ?"
        );
        List<Object> parameters = new ArrayList<>();
        parameters.add(Date.valueOf(startDate));
        parameters.add(Date.valueOf(endDate));
        if (employeeId != null) {
            sql.append(" AND v.EmployeeID = ?");
            parameters.add(employeeId);
        }
        if (clientId != null) {
            sql.append(" AND v.ClientID = ?");
            parameters.add(clientId);
        }
        sql.append(" GROUP BY " +
                  "    e.EmployeeID, " +
                  "    e.LastName, " +
                  "    e.FirstName, " +
                  "    e.MiddleName " +
                  "ORDER BY e.EmployeeID");
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
                Integer empId = rs.getInt("EmployeeID");
                String empName = rs.getString("EmployeeName");
                Integer totalVisits = rs.getInt("TotalVisits");
                BigDecimal totalCostSum = rs.getBigDecimal("TotalCostSum");
                Integer uniqueClients = rs.getInt("UniqueClients");
                String averageDuration = rs.getString("AverageVisitDuration");
                VisitsReport report = new VisitsReport(empId, empName, null, null, null, null);
                report.setTotalVisits(totalVisits);
                report.setTotalCostSum(totalCostSum);
                report.setUniqueClients(uniqueClients);
                report.setAverageVisitDurationSummary(averageDuration);
                list.add(report);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке итоговых данных отчета");
        }
        return list;
    }
}
