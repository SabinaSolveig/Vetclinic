package org.example.vetclinic.database;
import org.example.vetclinic.model.VisitService;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class VisitServiceDAO extends BaseDAO {
    public List<VisitService> getVisitServicesByVisit(Integer visitId) {
        List<VisitService> list = new ArrayList<>();
        if (visitId == null) {
            return list;
        }
        String sql = "SELECT vs.VisitID, vs.ServiceID, vs.Quantity, vs.Price, vs.Sum, " +
                     "vs.DiscountSum, vs.SumWithDiscount, s.ServiceName " +
                     "FROM VisitServices vs " +
                     "INNER JOIN Services s ON vs.ServiceID = s.ServiceID " +
                     "WHERE vs.VisitID = ? " +
                     "ORDER BY s.ServiceName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, visitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer vId = rs.getInt("VisitID");
                Integer serviceId = rs.getInt("ServiceID");
                Integer quantity = rs.getInt("Quantity");
                BigDecimal price = rs.getBigDecimal("Price");
                BigDecimal sum = rs.getBigDecimal("Sum");
                BigDecimal discountSum = rs.getBigDecimal("DiscountSum");
                BigDecimal sumWithDiscount = rs.getBigDecimal("SumWithDiscount");
                String serviceName = rs.getString("ServiceName");
                VisitService vs = new VisitService(vId, serviceId, quantity, price, sum, discountSum, sumWithDiscount, serviceName);
                list.add(vs);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке услуг приема");
        }
        return list;
    }
    public boolean addVisitService(VisitService visitService) {
        String sql = "INSERT INTO VisitServices (VisitID, ServiceID, Quantity, Price, Sum, DiscountSum) VALUES (?, ?, ?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, visitService.getVisitId());
                stmt.setInt(2, visitService.getServiceId());
                stmt.setInt(3, visitService.getQuantity());
                stmt.setBigDecimal(4, visitService.getPrice());
                stmt.setBigDecimal(5, visitService.getSum());
                stmt.setBigDecimal(6, visitService.getDiscountSum());
            },
            "Услуга приема успешно добавлена в базу данных",
            "Не удалось добавить услугу приема в базу данных",
            "при добавлении услуги приема"
        );
    }
    public boolean updateVisitService(VisitService visitService) {
        String sql = "UPDATE VisitServices SET Quantity = ?, Price = ?, Sum = ?, DiscountSum = ? WHERE VisitID = ? AND ServiceID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, visitService.getQuantity());
                stmt.setBigDecimal(2, visitService.getPrice());
                stmt.setBigDecimal(3, visitService.getSum());
                stmt.setBigDecimal(4, visitService.getDiscountSum());
                stmt.setInt(5, visitService.getVisitId());
                stmt.setInt(6, visitService.getServiceId());
            },
            "Услуга приема успешно обновлена в базе данных",
            "Не удалось обновить услугу приема в базе данных",
            "при обновлении услуги приема"
        );
    }
    public boolean deleteVisitService(Integer visitId, Integer serviceId) {
        if (visitId == null || serviceId == null) {
            System.err.println("Нельзя удалить услугу приема без ID");
            return false;
        }
        String sql = "DELETE FROM VisitServices WHERE VisitID = ? AND ServiceID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, visitId);
                stmt.setInt(2, serviceId);
            },
            "Услуга приема успешно удалена из базы данных",
            "Не удалось удалить услугу приема из базы данных",
            "при удалении услуги приема"
        );
    }
    public boolean deleteVisitService(VisitService visitService) {
        if (visitService == null) {
            System.err.println("Нельзя удалить услугу приема без данных");
            return false;
        }
        return deleteVisitService(visitService.getVisitId(), visitService.getServiceId());
    }
    public boolean deleteVisitServicesByVisit(Integer visitId) {
        if (visitId == null) {
            System.err.println("Нельзя удалить услуги приема без ID приема");
            return false;
        }
        String sql = "DELETE FROM VisitServices WHERE VisitID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, visitId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Удалено услуг приема: " + rowsAffected);
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении услуг приема из базы данных: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
