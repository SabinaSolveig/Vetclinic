package org.example.vetclinic.database;
import org.example.vetclinic.model.Service;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ServiceDAO extends BaseDAO {
    public List<Service> getAllServices() {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT s.ServiceID, s.ServiceName, s.ServiceCategoryID, s.Price, s.Description, " +
                     "sc.CategoryName " +
                     "FROM Services s " +
                     "LEFT JOIN ServiceCategories sc ON s.ServiceCategoryID = sc.ServiceCategoryID " +
                     "ORDER BY sc.CategoryName, s.ServiceName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("ServiceID");
                String serviceName = rs.getString("ServiceName");
                Integer categoryId = DAOUtils.getNullableInteger(rs, "ServiceCategoryID");
                BigDecimal price = rs.getBigDecimal("Price");
                String description = rs.getString("Description");
                String categoryName = rs.getString("CategoryName");
                Service service = new Service(id, serviceName, categoryId, price, description, categoryName);
                list.add(service);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке услуг");
        }
        return list;
    }
    public boolean addService(Service service) {
        String sql = "INSERT INTO Services (ServiceName, ServiceCategoryID, Price, Description) VALUES (?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, service.getServiceName());
                DAOUtils.setNullableInteger(stmt, 2, service.getServiceCategoryId());
                stmt.setBigDecimal(3, service.getPrice());
                DAOUtils.setNullableString(stmt, 4, service.getDescription());
            },
            "Услуга успешно добавлена в базу данных",
            "Не удалось добавить услугу в базу данных",
            "при добавлении услуги"
        );
    }
    public boolean updateService(Service service) {
        if (!validateId(service.getId(), "услугу")) {
            return false;
        }
        String sql = "UPDATE Services SET ServiceName = ?, ServiceCategoryID = ?, Price = ?, Description = ? WHERE ServiceID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, service.getServiceName());
                DAOUtils.setNullableInteger(stmt, 2, service.getServiceCategoryId());
                stmt.setBigDecimal(3, service.getPrice());
                DAOUtils.setNullableString(stmt, 4, service.getDescription());
                stmt.setInt(5, service.getId());
            },
            "Услуга успешно обновлена в базе данных",
            "Не удалось обновить услугу в базе данных (услуга не найдена)",
            "при обновлении услуги"
        );
    }
    public boolean deleteService(Integer serviceId) {
        if (!validateId(serviceId, "услугу")) {
            return false;
        }
        String sql = "DELETE FROM Services WHERE ServiceID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, serviceId),
            "Услуга успешно удалена из базы данных",
            "Не удалось удалить услугу из базы данных (услуга не найдена)",
            "при удалении услуги"
        );
    }
    public boolean deleteService(Service service) {
        if (service == null || service.getId() == null) {
            System.err.println("Нельзя удалить услугу без ID");
            return false;
        }
        return deleteService(service.getId());
    }
}
