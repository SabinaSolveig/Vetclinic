package org.example.vetclinic.database;
import org.example.vetclinic.model.Breed;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class BreedDAO extends BaseDAO {
    public List<Breed> getAllBreeds() {
        List<Breed> list = new ArrayList<>();
        String sql = "SELECT b.BreedID, b.SpeciesID, b.BreedName, s.SpeciesName " +
                     "FROM Breeds b " +
                     "INNER JOIN Species s ON b.SpeciesID = s.SpeciesID " +
                     "ORDER BY s.SpeciesName, b.BreedName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("BreedID");
                Integer speciesId = rs.getInt("SpeciesID");
                String breedName = rs.getString("BreedName");
                String speciesName = rs.getString("SpeciesName");
                Breed breed = new Breed(id, speciesId, breedName, speciesName);
                list.add(breed);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке пород");
        }
        return list;
    }
    public List<Breed> getBreedsBySpecies(Integer speciesId) {
        List<Breed> list = new ArrayList<>();
        if (speciesId == null) {
            return list;
        }
        String sql = "SELECT b.BreedID, b.SpeciesID, b.BreedName, s.SpeciesName " +
                     "FROM Breeds b " +
                     "INNER JOIN Species s ON b.SpeciesID = s.SpeciesID " +
                     "WHERE b.SpeciesID = ? " +
                     "ORDER BY b.BreedName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, speciesId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("BreedID");
                Integer breedSpeciesId = rs.getInt("SpeciesID");
                String breedName = rs.getString("BreedName");
                String speciesName = rs.getString("SpeciesName");
                Breed breed = new Breed(id, breedSpeciesId, breedName, speciesName);
                list.add(breed);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке пород по виду");
        }
        return list;
    }
    public boolean addBreed(Breed breed) {
        String sql = "INSERT INTO Breeds (SpeciesID, BreedName) VALUES (?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, breed.getSpeciesId());
                stmt.setString(2, breed.getBreedName());
            },
            "Порода успешно добавлена в базу данных",
            "Не удалось добавить породу в базу данных",
            "при добавлении породы"
        );
    }
    public boolean updateBreed(Breed breed) {
        if (!validateId(breed.getId(), "породу")) {
            return false;
        }
        String sql = "UPDATE Breeds SET SpeciesID = ?, BreedName = ? WHERE BreedID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, breed.getSpeciesId());
                stmt.setString(2, breed.getBreedName());
                stmt.setInt(3, breed.getId());
            },
            "Порода успешно обновлена в базе данных",
            "Не удалось обновить породу в базе данных (порода не найдена)",
            "при обновлении породы"
        );
    }
    public boolean deleteBreed(Integer breedId) {
        if (!validateId(breedId, "породу")) {
            return false;
        }
        String sql = "DELETE FROM Breeds WHERE BreedID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, breedId),
            "Порода успешно удалена из базы данных",
            "Не удалось удалить породу из базы данных (порода не найдена)",
            "при удалении породы"
        );
    }
    public boolean deleteBreed(Breed breed) {
        if (breed == null || breed.getId() == null) {
            System.err.println("Нельзя удалить породу без ID");
            return false;
        }
        return deleteBreed(breed.getId());
    }
}
