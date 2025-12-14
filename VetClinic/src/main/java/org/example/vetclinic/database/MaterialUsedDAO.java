package org.example.vetclinic.database;
import org.example.vetclinic.model.MaterialUsed;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class MaterialUsedDAO extends BaseDAO {
    public List<MaterialUsed> getMaterialsUsedByVisit(Integer visitId) {
        List<MaterialUsed> list = new ArrayList<>();
        if (visitId == null) {
            return list;
        }
        String sql = "SELECT mu.VisitID, mu.ProductID, mu.Quantity, p.ProductName " +
                     "FROM MaterialsUsed mu " +
                     "INNER JOIN Products p ON mu.ProductID = p.ProductID " +
                     "WHERE mu.VisitID = ? ORDER BY p.ProductName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, visitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer vId = rs.getInt("VisitID");
                Integer productId = rs.getInt("ProductID");
                BigDecimal quantity = rs.getBigDecimal("Quantity");
                String productName = rs.getString("ProductName");
                MaterialUsed mu = new MaterialUsed(vId, productId, quantity, productName);
                list.add(mu);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке использованных материалов");
        }
        return list;
    }
    public boolean addMaterialUsed(MaterialUsed materialUsed) {
        String sql = "INSERT INTO MaterialsUsed (VisitID, ProductID, Quantity) VALUES (?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, materialUsed.getVisitId());
                stmt.setInt(2, materialUsed.getProductId());
                stmt.setBigDecimal(3, materialUsed.getQuantity());
            },
            "Использованный материал успешно добавлен в базу данных",
            "Не удалось добавить использованный материал в базу данных",
            "при добавлении использованного материала"
        );
    }
    public boolean updateMaterialUsed(MaterialUsed materialUsed) {
        String sql = "UPDATE MaterialsUsed SET Quantity = ? WHERE VisitID = ? AND ProductID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setBigDecimal(1, materialUsed.getQuantity());
                stmt.setInt(2, materialUsed.getVisitId());
                stmt.setInt(3, materialUsed.getProductId());
            },
            "Использованный материал успешно обновлен в базе данных",
            "Не удалось обновить использованный материал в базе данных",
            "при обновлении использованного материала"
        );
    }
    public boolean deleteMaterialUsed(Integer visitId, Integer productId) {
        if (visitId == null || productId == null) {
            System.err.println("Нельзя удалить использованный материал без данных");
            return false;
        }
        String sql = "DELETE FROM MaterialsUsed WHERE VisitID = ? AND ProductID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, visitId);
                stmt.setInt(2, productId);
            },
            "Использованный материал успешно удален из базы данных",
            "Не удалось удалить использованный материал из базы данных",
            "при удалении использованного материала"
        );
    }
    public boolean deleteMaterialUsed(MaterialUsed materialUsed) {
        if (materialUsed == null) {
            System.err.println("Нельзя удалить использованный материал без данных");
            return false;
        }
        return deleteMaterialUsed(materialUsed.getVisitId(), materialUsed.getProductId());
    }
    public boolean deleteMaterialsUsedByVisit(Integer visitId) {
        if (visitId == null) {
            System.err.println("Нельзя удалить использованные материалы без ID приема");
            return false;
        }
        String sql = "DELETE FROM MaterialsUsed WHERE VisitID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, visitId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Удалено использованных материалов: " + rowsAffected);
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении использованных материалов из базы данных: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
