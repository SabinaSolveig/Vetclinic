package org.example.vetclinic.database;
import org.example.vetclinic.model.PreliminaryServiceSet;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class PreliminaryServiceSetDAO extends BaseDAO {
    public List<PreliminaryServiceSet> getPreliminaryServiceSetsByAppointment(Integer appointmentId) {
        List<PreliminaryServiceSet> list = new ArrayList<>();
        if (appointmentId == null) {
            return list;
        }
        String sql = "SELECT pss.PreliminaryServiceSetID, pss.AppointmentID, pss.ServiceID, " +
                     "pss.Quantity, pss.Notes, s.ServiceName, s.Price " +
                     "FROM PreliminaryServiceSets pss " +
                     "INNER JOIN Services s ON pss.ServiceID = s.ServiceID " +
                     "WHERE pss.AppointmentID = ? " +
                     "ORDER BY s.ServiceName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("PreliminaryServiceSetID");
                Integer appId = rs.getInt("AppointmentID");
                Integer serviceId = rs.getInt("ServiceID");
                Integer quantity = rs.getInt("Quantity");
                String notes = rs.getString("Notes");
                String serviceName = rs.getString("ServiceName");
                BigDecimal servicePrice = rs.getBigDecimal("Price");
                PreliminaryServiceSet pss = new PreliminaryServiceSet(id, appId, serviceId, quantity, notes,
                                                                      serviceName, servicePrice);
                list.add(pss);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке предварительных услуг");
        }
        return list;
    }
    public boolean addPreliminaryServiceSet(PreliminaryServiceSet pss) {
        String sql = "INSERT INTO PreliminaryServiceSets (AppointmentID, ServiceID, Quantity, Notes) VALUES (?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, pss.getAppointmentId());
                stmt.setInt(2, pss.getServiceId());
                stmt.setInt(3, pss.getQuantity());
                DAOUtils.setNullableString(stmt, 4, pss.getNotes());
            },
            "Предварительная услуга успешно добавлена в базу данных",
            "Не удалось добавить предварительную услугу в базу данных",
            "при добавлении предварительной услуги"
        );
    }
    public boolean updatePreliminaryServiceSet(PreliminaryServiceSet pss) {
        if (!validateId(pss.getId(), "предварительную услугу")) {
            return false;
        }
        String sql = "UPDATE PreliminaryServiceSets SET ServiceID = ?, Quantity = ?, Notes = ? WHERE PreliminaryServiceSetID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, pss.getServiceId());
                stmt.setInt(2, pss.getQuantity());
                DAOUtils.setNullableString(stmt, 3, pss.getNotes());
                stmt.setInt(4, pss.getId());
            },
            "Предварительная услуга успешно обновлена в базе данных",
            "Не удалось обновить предварительную услугу в базе данных (услуга не найдена)",
            "при обновлении предварительной услуги"
        );
    }
    public boolean deletePreliminaryServiceSet(Integer pssId) {
        if (!validateId(pssId, "предварительную услугу")) {
            return false;
        }
        String sql = "DELETE FROM PreliminaryServiceSets WHERE PreliminaryServiceSetID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, pssId),
            "Предварительная услуга успешно удалена из базы данных",
            "Не удалось удалить предварительную услугу из базы данных (услуга не найдена)",
            "при удалении предварительной услуги"
        );
    }
    public boolean deletePreliminaryServiceSet(PreliminaryServiceSet pss) {
        if (pss == null || pss.getId() == null) {
            System.err.println("Нельзя удалить предварительную услугу без ID");
            return false;
        }
        return deletePreliminaryServiceSet(pss.getId());
    }
    public boolean deletePreliminaryServiceSetsByAppointment(Integer appointmentId) {
        if (appointmentId == null) {
            System.err.println("Нельзя удалить предварительные услуги без ID заявки");
            return false;
        }
        String sql = "DELETE FROM PreliminaryServiceSets WHERE AppointmentID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, appointmentId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Удалено предварительных услуг: " + rowsAffected);
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении предварительных услуг из базы данных: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
