package org.example.vetclinic.database;
import org.example.vetclinic.model.Client;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ClientDAO extends BaseDAO {
    public List<Client> getClients() {
        List<Client> list = new ArrayList<>();
        String sql = "SELECT ClientID, FirstName, LastName, MiddleName, Address, DiscountPercent, Notes " +
                     "FROM Clients ORDER BY LastName, FirstName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("ClientID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String middleName = rs.getString("MiddleName");
                String address = rs.getString("Address");
                Integer discountPercent = DAOUtils.getNullableInteger(rs, "DiscountPercent");
                String notes = rs.getString("Notes");
                Client client = new Client(
                        id,
                        firstName,
                        lastName,
                        middleName,
                        address,
                        discountPercent,
                        notes
                );
                list.add(client);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке клиентов");
        }
        return list;
    }
    public boolean addClient(Client client) {
        String sql = "INSERT INTO Clients (FirstName, LastName, MiddleName, Address, DiscountPercent, Notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, client.getFirstName());
                stmt.setString(2, client.getLastName());
                DAOUtils.setNullableString(stmt, 3, client.getMiddleName());
                DAOUtils.setNullableString(stmt, 4, client.getAddress());
                DAOUtils.setNullableInteger(stmt, 5, client.getDiscountPercent());
                DAOUtils.setNullableString(stmt, 6, client.getNotes());
            },
            "Клиент успешно добавлен в базу данных",
            "Не удалось добавить клиента в базу данных",
            "при добавлении клиента"
        );
    }
    public boolean updateClient(Client client) {
        if (!validateId(client.getId(), "клиента")) {
            return false;
        }
        String sql = "UPDATE Clients SET FirstName = ?, LastName = ?, MiddleName = ?, " +
                     "Address = ?, DiscountPercent = ?, Notes = ? WHERE ClientID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, client.getFirstName());
                stmt.setString(2, client.getLastName());
                DAOUtils.setNullableString(stmt, 3, client.getMiddleName());
                DAOUtils.setNullableString(stmt, 4, client.getAddress());
                DAOUtils.setNullableInteger(stmt, 5, client.getDiscountPercent());
                DAOUtils.setNullableString(stmt, 6, client.getNotes());
                stmt.setInt(7, client.getId());
            },
            "Клиент успешно обновлен в базе данных",
            "Не удалось обновить клиента в базе данных (клиент не найден)",
            "при обновлении клиента"
        );
    }
    public boolean deleteClient(Integer clientId) {
        if (!validateId(clientId, "клиента")) {
            return false;
        }
        String sql = "DELETE FROM Clients WHERE ClientID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, clientId),
            "Клиент успешно удален из базы данных",
            "Не удалось удалить клиента из базы данных (клиент не найден)",
            "при удалении клиента"
        );
    }
    public boolean deleteClient(Client client) {
        if (client == null || client.getId() == null) {
            System.err.println("Нельзя удалить клиента без ID");
            return false;
        }
        return deleteClient(client.getId());
    }
    public List<Client> searchClients(String searchText) {
        List<Client> list = new ArrayList<>();
        if (searchText == null || searchText.trim().isEmpty()) {
            return getClients();
        }
        String sql = "SELECT ClientID, FirstName, LastName, MiddleName, Address, DiscountPercent, Notes " +
                     "FROM Clients " +
                     "WHERE LOWER(LastName) LIKE LOWER(?) OR LOWER(FirstName) LIKE LOWER(?) OR LOWER(COALESCE(MiddleName, '')) LIKE LOWER(?) " +
                     "ORDER BY LastName, FirstName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + searchText.trim() + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("ClientID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String middleName = rs.getString("MiddleName");
                String address = rs.getString("Address");
                Integer discountPercent = DAOUtils.getNullableInteger(rs, "DiscountPercent");
                String notes = rs.getString("Notes");
                Client client = new Client(
                        id,
                        firstName,
                        lastName,
                        middleName,
                        address,
                        discountPercent,
                        notes
                );
                list.add(client);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при поиске клиентов");
        }
        return list;
    }
}
