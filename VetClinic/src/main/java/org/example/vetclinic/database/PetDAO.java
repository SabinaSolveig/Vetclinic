package org.example.vetclinic.database;
import org.example.vetclinic.model.Pet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class PetDAO extends BaseDAO {
    public List<Pet> getAllPets() {
        List<Pet> list = new ArrayList<>();
        String sql = "SELECT p.PetID, p.ClientID, p.Name, p.SpeciesID, p.BreedID, " +
                     "p.BirthDate, p.Age, p.Gender, p.Notes, " +
                     "c.FirstName || ' ' || c.LastName as ClientName, " +
                     "s.SpeciesName, b.BreedName " +
                     "FROM Pets p " +
                     "INNER JOIN Clients c ON p.ClientID = c.ClientID " +
                     "LEFT JOIN Species s ON p.SpeciesID = s.SpeciesID " +
                     "LEFT JOIN Breeds b ON p.BreedID = b.BreedID " +
                     "ORDER BY c.LastName, c.FirstName, p.Name";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("PetID");
                Integer clientId = rs.getInt("ClientID");
                String name = rs.getString("Name");
                Integer speciesId = DAOUtils.getNullableInteger(rs, "SpeciesID");
                Integer breedId = DAOUtils.getNullableInteger(rs, "BreedID");
                LocalDate birthDate = null;
                Date date = rs.getDate("BirthDate");
                if (date != null) {
                    birthDate = date.toLocalDate();
                }
                Integer age = DAOUtils.getNullableInteger(rs, "Age");
                String gender = rs.getString("Gender");
                String notes = rs.getString("Notes");
                String clientName = rs.getString("ClientName");
                String speciesName = rs.getString("SpeciesName");
                String breedName = rs.getString("BreedName");
                Pet pet = new Pet(id, clientId, name, speciesId, breedId, birthDate, age, gender, notes,
                                 clientName, speciesName, breedName);
                list.add(pet);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке животных");
        }
        return list;
    }
    public boolean addPet(Pet pet) {
        String sql = "INSERT INTO Pets (ClientID, Name, SpeciesID, BreedID, BirthDate, Age, Gender, Notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, pet.getClientId());
                stmt.setString(2, pet.getName());
                DAOUtils.setNullableInteger(stmt, 3, pet.getSpeciesId());
                DAOUtils.setNullableInteger(stmt, 4, pet.getBreedId());
                DAOUtils.setNullableDate(stmt, 5, pet.getBirthDate() != null ? Date.valueOf(pet.getBirthDate()) : null);
                DAOUtils.setNullableInteger(stmt, 6, pet.getAge());
                DAOUtils.setNullableString(stmt, 7, pet.getGender());
                DAOUtils.setNullableString(stmt, 8, pet.getNotes());
            },
            "Животное успешно добавлено в базу данных",
            "Не удалось добавить животное в базу данных",
            "при добавлении животного"
        );
    }
    public boolean updatePet(Pet pet) {
        if (!validateId(pet.getId(), "животное")) {
            return false;
        }
        String sql = "UPDATE Pets SET ClientID = ?, Name = ?, SpeciesID = ?, BreedID = ?, " +
                     "BirthDate = ?, Age = ?, Gender = ?, Notes = ? WHERE PetID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, pet.getClientId());
                stmt.setString(2, pet.getName());
                DAOUtils.setNullableInteger(stmt, 3, pet.getSpeciesId());
                DAOUtils.setNullableInteger(stmt, 4, pet.getBreedId());
                DAOUtils.setNullableDate(stmt, 5, pet.getBirthDate() != null ? Date.valueOf(pet.getBirthDate()) : null);
                DAOUtils.setNullableInteger(stmt, 6, pet.getAge());
                DAOUtils.setNullableString(stmt, 7, pet.getGender());
                DAOUtils.setNullableString(stmt, 8, pet.getNotes());
                stmt.setInt(9, pet.getId());
            },
            "Животное успешно обновлено в базе данных",
            "Не удалось обновить животное в базе данных (животное не найдено)",
            "при обновлении животного"
        );
    }
    public boolean deletePet(Integer petId) {
        if (!validateId(petId, "животное")) {
            return false;
        }
        String sql = "DELETE FROM Pets WHERE PetID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, petId),
            "Животное успешно удалено из базы данных",
            "Не удалось удалить животное из базы данных (животное не найдено)",
            "при удалении животного"
        );
    }
    public boolean deletePet(Pet pet) {
        if (pet == null || pet.getId() == null) {
            System.err.println("Нельзя удалить животное без ID");
            return false;
        }
        return deletePet(pet.getId());
    }
}
