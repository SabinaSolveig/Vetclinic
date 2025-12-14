package org.example.vetclinic.database;
import org.example.vetclinic.model.VisitProduct;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class VisitProductDAO extends BaseDAO {
    public List<VisitProduct> getVisitProductsByVisit(Integer visitId) {
        List<VisitProduct> list = new ArrayList<>();
        if (visitId == null) {
            return list;
        }
        String sql = "SELECT vp.VisitID, vp.ProductID, vp.Quantity, vp.Price, vp.Sum, p.ProductName " +
                     "FROM VisitProducts vp " +
                     "INNER JOIN Products p ON vp.ProductID = p.ProductID " +
                     "WHERE vp.VisitID = ? " +
                     "ORDER BY p.ProductName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, visitId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer vId = rs.getInt("VisitID");
                Integer productId = rs.getInt("ProductID");
                Integer quantity = rs.getInt("Quantity");
                BigDecimal price = rs.getBigDecimal("Price");
                BigDecimal sum = rs.getBigDecimal("Sum");
                String productName = rs.getString("ProductName");
                VisitProduct vp = new VisitProduct(vId, productId, quantity, price, sum, productName);
                list.add(vp);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке товаров приема");
        }
        return list;
    }
    public boolean addVisitProduct(VisitProduct visitProduct) {
        String sql = "INSERT INTO VisitProducts (VisitID, ProductID, Quantity, Price) VALUES (?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, visitProduct.getVisitId());
                stmt.setInt(2, visitProduct.getProductId());
                stmt.setInt(3, visitProduct.getQuantity());
                stmt.setBigDecimal(4, visitProduct.getPrice());
            },
            "Товар приема успешно добавлен в базу данных",
            "Не удалось добавить товар приема в базу данных",
            "при добавлении товара приема"
        );
    }
    public boolean updateVisitProduct(VisitProduct visitProduct) {
        String sql = "UPDATE VisitProducts SET Quantity = ?, Price = ? WHERE VisitID = ? AND ProductID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, visitProduct.getQuantity());
                stmt.setBigDecimal(2, visitProduct.getPrice());
                stmt.setInt(3, visitProduct.getVisitId());
                stmt.setInt(4, visitProduct.getProductId());
            },
            "Товар приема успешно обновлен в базе данных",
            "Не удалось обновить товар приема в базе данных",
            "при обновлении товара приема"
        );
    }
    public boolean deleteVisitProduct(Integer visitId, Integer productId) {
        if (visitId == null || productId == null) {
            System.err.println("Нельзя удалить товар приема без ID");
            return false;
        }
        String sql = "DELETE FROM VisitProducts WHERE VisitID = ? AND ProductID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, visitId);
                stmt.setInt(2, productId);
            },
            "Товар приема успешно удален из базы данных",
            "Не удалось удалить товар приема из базы данных",
            "при удалении товара приема"
        );
    }
    public boolean deleteVisitProduct(VisitProduct visitProduct) {
        if (visitProduct == null) {
            System.err.println("Нельзя удалить товар приема без данных");
            return false;
        }
        return deleteVisitProduct(visitProduct.getVisitId(), visitProduct.getProductId());
    }
    public boolean deleteVisitProductsByVisit(Integer visitId) {
        if (visitId == null) {
            System.err.println("Нельзя удалить товары приема без ID приема");
            return false;
        }
        String sql = "DELETE FROM VisitProducts WHERE VisitID = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, visitId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Удалено товаров приема: " + rowsAffected);
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при удалении товаров приема из базы данных: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
