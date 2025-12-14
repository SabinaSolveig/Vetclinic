package org.example.vetclinic.database;
import org.example.vetclinic.model.Species;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class SpeciesDAO extends BaseDAO {
    public List<Species> getAllSpecies() {
        List<Species> list = new ArrayList<>();
        String sql = "SELECT SpeciesID, SpeciesName FROM Species ORDER BY SpeciesName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("SpeciesID");
                String name = rs.getString("SpeciesName");
                Species species = new Species(id, name);
                list.add(species);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке видов животных");
        }
        return list;
    }
    public boolean addSpecies(Species species) {
        String sql = "INSERT INTO Species (SpeciesName) VALUES (?)";
        return executeUpdate(
            sql,
            stmt -> stmt.setString(1, species.getSpeciesName()),
            "Вид животного успешно добавлен в базу данных",
            "Не удалось добавить вид животного в базу данных",
            "при добавлении вида животного"
        );
    }
    public boolean updateSpecies(Species species) {
        if (!validateId(species.getId(), "вид животного")) {
            return false;
        }
        String sql = "UPDATE Species SET SpeciesName = ? WHERE SpeciesID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, species.getSpeciesName());
                stmt.setInt(2, species.getId());
            },
            "Вид животного успешно обновлен в базе данных",
            "Не удалось обновить вид животного в базе данных (вид не найден)",
            "при обновлении вида животного"
        );
    }
    public boolean deleteSpecies(Integer speciesId) {
        if (!validateId(speciesId, "вид животного")) {
            return false;
        }
        String sql = "DELETE FROM Species WHERE SpeciesID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, speciesId),
            "Вид животного успешно удален из базы данных",
            "Не удалось удалить вид животного из базы данных (вид не найден)",
            "при удалении вида животного"
        );
    }
    public boolean deleteSpecies(Species species) {
        if (species == null || species.getId() == null) {
            System.err.println("Нельзя удалить вид животного без ID");
            return false;
        }
        return deleteSpecies(species.getId());
    }
}
