package org.example.vetclinic.database;
import org.example.vetclinic.model.ContactType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ContactTypeDAO extends BaseDAO {
    public List<ContactType> getAllContactTypes() {
        List<ContactType> list = new ArrayList<>();
        String sql = "SELECT ContactTypeID, ContactTypeName, Description FROM ContactTypes ORDER BY ContactTypeName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("ContactTypeID");
                String name = rs.getString("ContactTypeName");
                String description = rs.getString("Description");
                ContactType contactType = new ContactType(id, name, description);
                list.add(contactType);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке видов контактной информации");
        }
        return list;
    }
    public boolean addContactType(ContactType contactType) {
        String sql = "INSERT INTO ContactTypes (ContactTypeName, Description) VALUES (?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, contactType.getContactTypeName());
                DAOUtils.setNullableString(stmt, 2, contactType.getDescription());
            },
            "Вид контактной информации успешно добавлен в базу данных",
            "Не удалось добавить вид контактной информации в базу данных",
            "при добавлении вида контактной информации"
        );
    }
    public boolean updateContactType(ContactType contactType) {
        if (!validateId(contactType.getId(), "вид контактной информации")) {
            return false;
        }
        String sql = "UPDATE ContactTypes SET ContactTypeName = ?, Description = ? WHERE ContactTypeID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, contactType.getContactTypeName());
                DAOUtils.setNullableString(stmt, 2, contactType.getDescription());
                stmt.setInt(3, contactType.getId());
            },
            "Вид контактной информации успешно обновлен в базе данных",
            "Не удалось обновить вид контактной информации в базе данных (вид не найден)",
            "при обновлении вида контактной информации"
        );
    }
    public boolean deleteContactType(Integer contactTypeId) {
        if (!validateId(contactTypeId, "вид контактной информации")) {
            return false;
        }
        String sql = "DELETE FROM ContactTypes WHERE ContactTypeID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, contactTypeId),
            "Вид контактной информации успешно удален из базы данных",
            "Не удалось удалить вид контактной информации из базы данных (вид не найден)",
            "при удалении вида контактной информации"
        );
    }
    public boolean deleteContactType(ContactType contactType) {
        if (contactType == null || contactType.getId() == null) {
            System.err.println("Нельзя удалить вид контактной информации без ID");
            return false;
        }
        return deleteContactType(contactType.getId());
    }
}
