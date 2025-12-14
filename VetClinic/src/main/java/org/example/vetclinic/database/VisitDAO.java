package org.example.vetclinic.database;
import org.example.vetclinic.model.Visit;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
public class VisitDAO extends BaseDAO {
    public List<Visit> getAllVisits() {
        List<Visit> list = new ArrayList<>();
        String sql = "SELECT v.VisitID, v.AppointmentID, v.ClientID, v.PetID, v.EmployeeID, " +
                     "v.VisitDate, v.StartTime, v.EndTime, v.Diagnosis, v.Anamnesis, " +
                     "v.Treatment, v.Recommendations, v.TotalCost, " +
                     "c.LastName || ' ' || c.FirstName as ClientName, " +
                     "p.Name as PetName, " +
                     "e.LastName || ' ' || e.FirstName as EmployeeName, " +
                     "CASE WHEN EXISTS (SELECT 1 FROM Payments WHERE VisitID = v.VisitID) " +
                     "THEN 'Оплачено' ELSE 'Не оплачен' END as PaymentStatus " +
                     "FROM Visits v " +
                     "INNER JOIN Clients c ON v.ClientID = c.ClientID " +
                     "INNER JOIN Pets p ON v.PetID = p.PetID " +
                     "INNER JOIN Employees e ON v.EmployeeID = e.EmployeeID " +
                     "ORDER BY v.VisitDate DESC, v.StartTime DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("VisitID");
                Integer appointmentId = rs.getObject("AppointmentID") != null ? rs.getInt("AppointmentID") : null;
                Integer clientId = rs.getInt("ClientID");
                Integer petId = rs.getInt("PetID");
                Integer employeeId = rs.getInt("EmployeeID");
                LocalDate visitDate = null;
                Date date = rs.getDate("VisitDate");
                if (date != null) {
                    visitDate = date.toLocalDate();
                }
                LocalTime startTime = null;
                Time time = rs.getTime("StartTime");
                if (time != null) {
                    startTime = time.toLocalTime();
                }
                LocalTime endTime = null;
                Time endTimeValue = rs.getTime("EndTime");
                if (endTimeValue != null) {
                    endTime = endTimeValue.toLocalTime();
                }
                String diagnosis = rs.getString("Diagnosis");
                String anamnesis = rs.getString("Anamnesis");
                String treatment = rs.getString("Treatment");
                String recommendations = rs.getString("Recommendations");
                BigDecimal totalCost = rs.getBigDecimal("TotalCost");
                String clientName = rs.getString("ClientName");
                String petName = rs.getString("PetName");
                String employeeName = rs.getString("EmployeeName");
                String paymentStatus = rs.getString("PaymentStatus");
                Visit visit = new Visit(id, appointmentId, clientId, petId, employeeId,
                                       visitDate, startTime, endTime, diagnosis, anamnesis,
                                       treatment, recommendations, totalCost,
                                       clientName, petName, employeeName, paymentStatus);
                list.add(visit);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке приемов");
        }
        return list;
    }
    public List<Visit> getVisitsWithFilters(Integer employeeId, Integer clientId, Integer petId,
                                           String sortField, boolean ascending) {
        List<Visit> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT v.VisitID, v.AppointmentID, v.ClientID, v.PetID, v.EmployeeID, " +
            "v.VisitDate, v.StartTime, v.EndTime, v.Diagnosis, v.Anamnesis, " +
            "v.Treatment, v.Recommendations, v.TotalCost, " +
            "c.LastName || ' ' || c.FirstName as ClientName, " +
            "p.Name as PetName, " +
            "e.LastName || ' ' || e.FirstName as EmployeeName, " +
            "CASE WHEN EXISTS (SELECT 1 FROM Payments WHERE VisitID = v.VisitID) " +
            "THEN 'Оплачено' ELSE 'Не оплачен' END as PaymentStatus " +
            "FROM Visits v " +
            "INNER JOIN Clients c ON v.ClientID = c.ClientID " +
            "INNER JOIN Pets p ON v.PetID = p.PetID " +
            "INNER JOIN Employees e ON v.EmployeeID = e.EmployeeID " +
            "WHERE 1=1"
        );
        List<Object> parameters = new ArrayList<>();
        if (employeeId != null) {
            sql.append(" AND v.EmployeeID = ?");
            parameters.add(employeeId);
        }
        if (clientId != null) {
            sql.append(" AND v.ClientID = ?");
            parameters.add(clientId);
        }
        if (petId != null) {
            sql.append(" AND v.PetID = ?");
            parameters.add(petId);
        }
        sql.append(" ORDER BY ");
        String actualSortField = "v.VisitDate";
        if (sortField != null) {
            switch (sortField) {
                case "Дата":
                    actualSortField = "v.VisitDate";
                    break;
                case "Время начала":
                    actualSortField = "v.StartTime";
                    break;
                case "Клиент":
                    actualSortField = "ClientName";
                    break;
                case "Питомец":
                    actualSortField = "PetName";
                    break;
                case "Врач":
                    actualSortField = "EmployeeName";
                    break;
                case "Стоимость":
                    actualSortField = "v.TotalCost";
                    break;
                default:
                    actualSortField = "v.VisitDate";
                    break;
            }
        }
        sql.append(actualSortField).append(ascending ? " ASC" : " DESC");
        if (!actualSortField.equals("v.VisitDate") && !actualSortField.equals("v.StartTime")) {
            sql.append(", v.VisitDate DESC, v.StartTime DESC");
        }
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                }
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("VisitID");
                Integer appointmentId = rs.getObject("AppointmentID") != null ? rs.getInt("AppointmentID") : null;
                Integer cId = rs.getInt("ClientID");
                Integer pId = rs.getInt("PetID");
                Integer empId = rs.getInt("EmployeeID");
                LocalDate visitDate = null;
                Date date = rs.getDate("VisitDate");
                if (date != null) {
                    visitDate = date.toLocalDate();
                }
                LocalTime startTime = null;
                Time time = rs.getTime("StartTime");
                if (time != null) {
                    startTime = time.toLocalTime();
                }
                LocalTime endTime = null;
                Time endTimeValue = rs.getTime("EndTime");
                if (endTimeValue != null) {
                    endTime = endTimeValue.toLocalTime();
                }
                String diagnosis = rs.getString("Diagnosis");
                String anamnesis = rs.getString("Anamnesis");
                String treatment = rs.getString("Treatment");
                String recommendations = rs.getString("Recommendations");
                BigDecimal totalCost = rs.getBigDecimal("TotalCost");
                String clientName = rs.getString("ClientName");
                String petName = rs.getString("PetName");
                String employeeName = rs.getString("EmployeeName");
                String paymentStatus = rs.getString("PaymentStatus");
                Visit visit = new Visit(id, appointmentId, cId, pId, empId,
                                       visitDate, startTime, endTime, diagnosis, anamnesis,
                                       treatment, recommendations, totalCost,
                                       clientName, petName, employeeName, paymentStatus);
                list.add(visit);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при загрузке приемов из базы данных с фильтрами: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }
    public boolean addVisit(Visit visit) {
        String sql = "INSERT INTO Visits (AppointmentID, ClientID, PetID, EmployeeID, VisitDate, StartTime, EndTime, " +
                     "Diagnosis, Anamnesis, Treatment, Recommendations, TotalCost) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                DAOUtils.setNullableInteger(stmt, 1, visit.getAppointmentId());
                stmt.setInt(2, visit.getClientId());
                stmt.setInt(3, visit.getPetId());
                stmt.setInt(4, visit.getEmployeeId());
                stmt.setDate(5, visit.getVisitDate() != null ? Date.valueOf(visit.getVisitDate()) : null);
                if (visit.getStartTime() != null) {
                    stmt.setTime(6, Time.valueOf(visit.getStartTime()));
                } else {
                    stmt.setNull(6, java.sql.Types.TIME);
                }
                if (visit.getEndTime() != null) {
                    stmt.setTime(7, Time.valueOf(visit.getEndTime()));
                } else {
                    stmt.setNull(7, java.sql.Types.TIME);
                }
                DAOUtils.setNullableString(stmt, 8, visit.getDiagnosis());
                DAOUtils.setNullableString(stmt, 9, visit.getAnamnesis());
                DAOUtils.setNullableString(stmt, 10, visit.getTreatment());
                DAOUtils.setNullableString(stmt, 11, visit.getRecommendations());
                stmt.setBigDecimal(12, visit.getTotalCost());
            },
            "Прием успешно добавлен в базу данных",
            "Не удалось добавить прием в базу данных",
            "при добавлении приема"
        );
    }
    public boolean updateVisit(Visit visit) {
        if (!validateId(visit.getId(), "прием")) {
            return false;
        }
        String sql = "UPDATE Visits SET AppointmentID = ?, ClientID = ?, PetID = ?, EmployeeID = ?, " +
                     "VisitDate = ?, StartTime = ?, EndTime = ?, Diagnosis = ?, Anamnesis = ?, " +
                     "Treatment = ?, Recommendations = ?, TotalCost = ? WHERE VisitID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                DAOUtils.setNullableInteger(stmt, 1, visit.getAppointmentId());
                stmt.setInt(2, visit.getClientId());
                stmt.setInt(3, visit.getPetId());
                stmt.setInt(4, visit.getEmployeeId());
                stmt.setDate(5, visit.getVisitDate() != null ? Date.valueOf(visit.getVisitDate()) : null);
                if (visit.getStartTime() != null) {
                    stmt.setTime(6, Time.valueOf(visit.getStartTime()));
                } else {
                    stmt.setNull(6, java.sql.Types.TIME);
                }
                if (visit.getEndTime() != null) {
                    stmt.setTime(7, Time.valueOf(visit.getEndTime()));
                } else {
                    stmt.setNull(7, java.sql.Types.TIME);
                }
                DAOUtils.setNullableString(stmt, 8, visit.getDiagnosis());
                DAOUtils.setNullableString(stmt, 9, visit.getAnamnesis());
                DAOUtils.setNullableString(stmt, 10, visit.getTreatment());
                DAOUtils.setNullableString(stmt, 11, visit.getRecommendations());
                stmt.setBigDecimal(12, visit.getTotalCost());
                stmt.setInt(13, visit.getId());
            },
            "Прием успешно обновлен в базе данных",
            "Не удалось обновить прием в базе данных (прием не найден)",
            "при обновлении приема"
        );
    }
    public boolean deleteVisit(Integer visitId) {
        if (!validateId(visitId, "прием")) {
            return false;
        }
        String sql = "DELETE FROM Visits WHERE VisitID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, visitId),
            "Прием успешно удален из базы данных",
            "Не удалось удалить прием из базы данных (прием не найден)",
            "при удалении приема"
        );
    }
    public boolean deleteVisit(Visit visit) {
        if (visit == null || visit.getId() == null) {
            System.err.println("Нельзя удалить прием без ID");
            return false;
        }
        return deleteVisit(visit.getId());
    }
}
