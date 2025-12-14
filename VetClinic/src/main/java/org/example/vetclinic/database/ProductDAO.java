package org.example.vetclinic.database;
import org.example.vetclinic.model.Product;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ProductDAO extends BaseDAO {
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.ProductName, p.ProductCategoryID, p.Price, p.StockQuantity, " +
                     "pc.CategoryName " +
                     "FROM Products p " +
                     "LEFT JOIN ProductCategories pc ON p.ProductCategoryID = pc.ProductCategoryID " +
                     "ORDER BY pc.CategoryName, p.ProductName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("ProductID");
                String productName = rs.getString("ProductName");
                Integer categoryId = DAOUtils.getNullableInteger(rs, "ProductCategoryID");
                BigDecimal price = rs.getBigDecimal("Price");
                Integer stockQuantity = rs.getInt("StockQuantity");
                String categoryName = rs.getString("CategoryName");
                Product product = new Product(id, productName, categoryId, price, stockQuantity, categoryName);
                list.add(product);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке товаров");
        }
        return list;
    }
    public boolean addProduct(Product product) {
        String sql = "INSERT INTO Products (ProductName, ProductCategoryID, Price, StockQuantity) VALUES (?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, product.getProductName());
                DAOUtils.setNullableInteger(stmt, 2, product.getProductCategoryId());
                stmt.setBigDecimal(3, product.getPrice());
                stmt.setInt(4, product.getStockQuantity());
            },
            "Товар успешно добавлен в базу данных",
            "Не удалось добавить товар в базу данных",
            "при добавлении товара"
        );
    }
    public boolean updateProduct(Product product) {
        if (!validateId(product.getId(), "товар")) {
            return false;
        }
        String sql = "UPDATE Products SET ProductName = ?, ProductCategoryID = ?, Price = ?, StockQuantity = ? WHERE ProductID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, product.getProductName());
                DAOUtils.setNullableInteger(stmt, 2, product.getProductCategoryId());
                stmt.setBigDecimal(3, product.getPrice());
                stmt.setInt(4, product.getStockQuantity());
                stmt.setInt(5, product.getId());
            },
            "Товар успешно обновлен в базе данных",
            "Не удалось обновить товар в базе данных (товар не найден)",
            "при обновлении товара"
        );
    }
    public boolean deleteProduct(Integer productId) {
        if (!validateId(productId, "товар")) {
            return false;
        }
        String sql = "DELETE FROM Products WHERE ProductID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, productId),
            "Товар успешно удален из базы данных",
            "Не удалось удалить товар из базы данных (товар не найден)",
            "при удалении товара"
        );
    }
    public boolean deleteProduct(Product product) {
        if (product == null || product.getId() == null) {
            System.err.println("Нельзя удалить товар без ID");
            return false;
        }
        return deleteProduct(product.getId());
    }
}
