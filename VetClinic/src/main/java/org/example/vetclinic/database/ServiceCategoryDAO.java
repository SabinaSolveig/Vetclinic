package org.example.vetclinic.database;
import org.example.vetclinic.model.ServiceCategory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ServiceCategoryDAO extends BaseDAO {
    public List<ServiceCategory> getAllServiceCategories() {
        List<ServiceCategory> list = new ArrayList<>();
        String sql = "SELECT ServiceCategoryID, CategoryName FROM ServiceCategories ORDER BY CategoryName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("ServiceCategoryID");
                String name = rs.getString("CategoryName");
                ServiceCategory category = new ServiceCategory(id, name);
                list.add(category);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке категорий услуг");
        }
        return list;
    }
    public boolean addServiceCategory(ServiceCategory category) {
        String sql = "INSERT INTO ServiceCategories (CategoryName) VALUES (?)";
        return executeUpdate(
            sql,
            stmt -> stmt.setString(1, category.getCategoryName()),
            "Категория услуг успешно добавлена в базу данных",
            "Не удалось добавить категорию услуг в базу данных",
            "при добавлении категории услуг"
        );
    }
    public boolean updateServiceCategory(ServiceCategory category) {
        if (!validateId(category.getId(), "категорию услуг")) {
            return false;
        }
        String sql = "UPDATE ServiceCategories SET CategoryName = ? WHERE ServiceCategoryID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, category.getCategoryName());
                stmt.setInt(2, category.getId());
            },
            "Категория услуг успешно обновлена в базе данных",
            "Не удалось обновить категорию услуг в базе данных (категория не найдена)",
            "при обновлении категории услуг"
        );
    }
    public boolean deleteServiceCategory(Integer categoryId) {
        if (!validateId(categoryId, "категорию услуг")) {
            return false;
        }
        String sql = "DELETE FROM ServiceCategories WHERE ServiceCategoryID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, categoryId),
            "Категория услуг успешно удалена из базы данных",
            "Не удалось удалить категорию услуг из базы данных (категория не найдена)",
            "при удалении категории услуг"
        );
    }
    public boolean deleteServiceCategory(ServiceCategory category) {
        if (category == null || category.getId() == null) {
            System.err.println("Нельзя удалить категорию услуг без ID");
            return false;
        }
        return deleteServiceCategory(category.getId());
    }
}
