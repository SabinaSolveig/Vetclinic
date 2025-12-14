package org.example.vetclinic.database;
import org.example.vetclinic.model.ContactInfo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class ContactInfoDAO extends BaseDAO {
    public List<ContactInfo> getContactInfoByClient(Integer clientId) {
        List<ContactInfo> list = new ArrayList<>();
        if (clientId == null) {
            return list;
        }
        String sql = "SELECT c.ContactID, c.OwnerType, c.OwnerID, c.ContactTypeID, c.ContactValue, " +
                     "c.IsPrimary, c.Notes, ct.ContactTypeName " +
                     "FROM ContactInfo c " +
                     "LEFT JOIN ContactTypes ct ON c.ContactTypeID = ct.ContactTypeID " +
                     "WHERE c.OwnerType = 'Client' AND c.OwnerID = ? " +
                     "ORDER BY c.IsPrimary DESC, ct.ContactTypeName, c.ContactValue";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("ContactID");
                String ownerType = rs.getString("OwnerType");
                Integer ownerId = rs.getInt("OwnerID");
                Integer contactTypeId = DAOUtils.getNullableInteger(rs, "ContactTypeID");
                String contactValue = rs.getString("ContactValue");
                Boolean isPrimary = rs.getBoolean("IsPrimary");
                String notes = rs.getString("Notes");
                String contactTypeName = rs.getString("ContactTypeName");
                ContactInfo contactInfo = new ContactInfo(id, ownerType, ownerId, contactTypeId,
                                                          contactValue, isPrimary, notes, contactTypeName);
                list.add(contactInfo);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке контактной информации");
        }
        return list;
    }
    public boolean addContactInfo(ContactInfo contactInfo) {
        String sql = "INSERT INTO ContactInfo (OwnerType, OwnerID, ContactTypeID, ContactValue, IsPrimary, Notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, contactInfo.getOwnerType());
                stmt.setInt(2, contactInfo.getOwnerId());
                DAOUtils.setNullableInteger(stmt, 3, contactInfo.getContactTypeId());
                stmt.setString(4, contactInfo.getContactValue());
                stmt.setBoolean(5, contactInfo.getIsPrimary() != null ? contactInfo.getIsPrimary() : false);
                DAOUtils.setNullableString(stmt, 6, contactInfo.getNotes());
            },
            "Контактная информация успешно добавлена в базу данных",
            "Не удалось добавить контактную информацию в базу данных",
            "при добавлении контактной информации"
        );
    }
    public boolean updateContactInfo(ContactInfo contactInfo) {
        if (!validateId(contactInfo.getId(), "контактную информацию")) {
            return false;
        }
        String sql = "UPDATE ContactInfo SET ContactTypeID = ?, ContactValue = ?, IsPrimary = ?, Notes = ? " +
                     "WHERE ContactID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                DAOUtils.setNullableInteger(stmt, 1, contactInfo.getContactTypeId());
                stmt.setString(2, contactInfo.getContactValue());
                stmt.setBoolean(3, contactInfo.getIsPrimary() != null ? contactInfo.getIsPrimary() : false);
                DAOUtils.setNullableString(stmt, 4, contactInfo.getNotes());
                stmt.setInt(5, contactInfo.getId());
            },
            "Контактная информация успешно обновлена в базе данных",
            "Не удалось обновить контактную информацию в базе данных (контакт не найден)",
            "при обновлении контактной информации"
        );
    }
    public boolean deleteContactInfo(Integer contactId) {
        if (!validateId(contactId, "контактную информацию")) {
            return false;
        }
        String sql = "DELETE FROM ContactInfo WHERE ContactID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, contactId),
            "Контактная информация успешно удалена из базы данных",
            "Не удалось удалить контактную информацию из базы данных (контакт не найден)",
            "при удалении контактной информации"
        );
    }
    public boolean deleteContactInfo(ContactInfo contactInfo) {
        if (contactInfo == null || contactInfo.getId() == null) {
            System.err.println("Нельзя удалить контактную информацию без ID");
            return false;
        }
        return deleteContactInfo(contactInfo.getId());
    }
}
