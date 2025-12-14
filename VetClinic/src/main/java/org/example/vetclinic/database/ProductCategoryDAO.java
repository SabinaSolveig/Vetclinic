package org.example.vetclinic.database;
import org.example.vetclinic.model.ProductCategory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ProductCategoryDAO extends BaseDAO {
    public List<ProductCategory> getAllProductCategories() {
        List<ProductCategory> list = new ArrayList<>();
        String sql = "SELECT ProductCategoryID, CategoryName FROM ProductCategories ORDER BY CategoryName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("ProductCategoryID");
                String name = rs.getString("CategoryName");
                ProductCategory category = new ProductCategory(id, name);
                list.add(category);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке категорий товаров");
        }
        return list;
    }
    public boolean addProductCategory(ProductCategory category) {
        String sql = "INSERT INTO ProductCategories (CategoryName) VALUES (?)";
        return executeUpdate(
            sql,
            stmt -> stmt.setString(1, category.getCategoryName()),
            "Категория товаров успешно добавлена в базу данных",
            "Не удалось добавить категорию товаров в базу данных",
            "при добавлении категории товаров"
        );
    }
    public boolean updateProductCategory(ProductCategory category) {
        if (!validateId(category.getId(), "категорию товаров")) {
            return false;
        }
        String sql = "UPDATE ProductCategories SET CategoryName = ? WHERE ProductCategoryID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, category.getCategoryName());
                stmt.setInt(2, category.getId());
            },
            "Категория товаров успешно обновлена в базе данных",
            "Не удалось обновить категорию товаров в базе данных (категория не найдена)",
            "при обновлении категории товаров"
        );
    }
    public boolean deleteProductCategory(Integer categoryId) {
        if (!validateId(categoryId, "категорию товаров")) {
            return false;
        }
        String sql = "DELETE FROM ProductCategories WHERE ProductCategoryID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, categoryId),
            "Категория товаров успешно удалена из базы данных",
            "Не удалось удалить категорию товаров из базы данных (категория не найдена)",
            "при удалении категории товаров"
        );
    }
    public boolean deleteProductCategory(ProductCategory category) {
        if (category == null || category.getId() == null) {
            System.err.println("Нельзя удалить категорию товаров без ID");
            return false;
        }
        return deleteProductCategory(category.getId());
    }
}
