package org.example.vetclinic.database;
import org.example.vetclinic.model.Specialization;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class SpecializationDAO extends BaseDAO {
    public List<Specialization> getAllSpecializations() {
        List<Specialization> list = new ArrayList<>();
        String sql = "SELECT SpecializationID, SpecializationName, Description FROM Specializations ORDER BY SpecializationName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("SpecializationID");
                String name = rs.getString("SpecializationName");
                String description = rs.getString("Description");
                Specialization specialization = new Specialization(id, name, description);
                list.add(specialization);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке специальностей");
        }
        return list;
    }
    public boolean addSpecialization(Specialization specialization) {
        String sql = "INSERT INTO Specializations (SpecializationName, Description) VALUES (?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, specialization.getSpecializationName());
                DAOUtils.setNullableString(stmt, 2, specialization.getDescription());
            },
            "Специальность успешно добавлена в базу данных",
            "Не удалось добавить специальность в базу данных",
            "при добавлении специальности"
        );
    }
    public boolean updateSpecialization(Specialization specialization) {
        if (!validateId(specialization.getId(), "специальность")) {
            return false;
        }
        String sql = "UPDATE Specializations SET SpecializationName = ?, Description = ? WHERE SpecializationID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, specialization.getSpecializationName());
                DAOUtils.setNullableString(stmt, 2, specialization.getDescription());
                stmt.setInt(3, specialization.getId());
            },
            "Специальность успешно обновлена в базе данных",
            "Не удалось обновить специальность в базе данных (специальность не найдена)",
            "при обновлении специальности"
        );
    }
    public boolean deleteSpecialization(Integer specializationId) {
        if (!validateId(specializationId, "специальность")) {
            return false;
        }
        String sql = "DELETE FROM Specializations WHERE SpecializationID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, specializationId),
            "Специальность успешно удалена из базы данных",
            "Не удалось удалить специальность из базы данных (специальность не найдена)",
            "при удалении специальности"
        );
    }
    public boolean deleteSpecialization(Specialization specialization) {
        if (specialization == null || specialization.getId() == null) {
            System.err.println("Нельзя удалить специальность без ID");
            return false;
        }
        return deleteSpecialization(specialization.getId());
    }
}
