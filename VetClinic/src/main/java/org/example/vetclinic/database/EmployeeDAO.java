package org.example.vetclinic.database;
import org.example.vetclinic.model.Employee;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class EmployeeDAO extends BaseDAO {
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT e.EmployeeID, e.LastName, e.FirstName, e.MiddleName, e.BirthDate, " +
                     "e.SpecializationID, e.HireDate, e.DismissalDate, e.Active, " +
                     "s.SpecializationName " +
                     "FROM Employees e " +
                     "LEFT JOIN Specializations s ON e.SpecializationID = s.SpecializationID " +
                     "ORDER BY e.LastName, e.FirstName";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("EmployeeID");
                String lastName = rs.getString("LastName");
                String firstName = rs.getString("FirstName");
                String middleName = rs.getString("MiddleName");
                LocalDate birthDate = null;
                Date date = rs.getDate("BirthDate");
                if (date != null) {
                    birthDate = date.toLocalDate();
                }
                Integer specializationId = DAOUtils.getNullableInteger(rs, "SpecializationID");
                LocalDate hireDate = null;
                Date hireDateSql = rs.getDate("HireDate");
                if (hireDateSql != null) {
                    hireDate = hireDateSql.toLocalDate();
                }
                LocalDate dismissalDate = null;
                Date dismissalDateSql = rs.getDate("DismissalDate");
                if (dismissalDateSql != null) {
                    dismissalDate = dismissalDateSql.toLocalDate();
                }
                Boolean active = rs.getBoolean("Active");
                String specializationName = rs.getString("SpecializationName");
                Employee employee = new Employee(id, firstName, lastName, middleName,
                                                birthDate, specializationId, hireDate,
                                                dismissalDate, active, specializationName);
                list.add(employee);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке сотрудников");
        }
        return list;
    }
    public boolean addEmployee(Employee employee) {
        String sql = "INSERT INTO Employees (LastName, FirstName, MiddleName, BirthDate, SpecializationID, HireDate, DismissalDate, Active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, employee.getLastName());
                stmt.setString(2, employee.getFirstName());
                DAOUtils.setNullableString(stmt, 3, employee.getMiddleName());
                DAOUtils.setNullableDate(stmt, 4, employee.getBirthDate() != null ? Date.valueOf(employee.getBirthDate()) : null);
                DAOUtils.setNullableInteger(stmt, 5, employee.getSpecializationId());
                stmt.setDate(6, employee.getHireDate() != null ? Date.valueOf(employee.getHireDate()) : Date.valueOf(LocalDate.now()));
                DAOUtils.setNullableDate(stmt, 7, employee.getDismissalDate() != null ? Date.valueOf(employee.getDismissalDate()) : null);
                stmt.setBoolean(8, employee.getActive() != null ? employee.getActive() : true);
            },
            "Сотрудник успешно добавлен в базу данных",
            "Не удалось добавить сотрудника в базу данных",
            "при добавлении сотрудника"
        );
    }
    public boolean updateEmployee(Employee employee) {
        if (!validateId(employee.getId(), "сотрудника")) {
            return false;
        }
        String sql = "UPDATE Employees SET LastName = ?, FirstName = ?, MiddleName = ?, BirthDate = ?, " +
                     "SpecializationID = ?, HireDate = ?, DismissalDate = ?, Active = ? WHERE EmployeeID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setString(1, employee.getLastName());
                stmt.setString(2, employee.getFirstName());
                DAOUtils.setNullableString(stmt, 3, employee.getMiddleName());
                DAOUtils.setNullableDate(stmt, 4, employee.getBirthDate() != null ? Date.valueOf(employee.getBirthDate()) : null);
                DAOUtils.setNullableInteger(stmt, 5, employee.getSpecializationId());
                stmt.setDate(6, employee.getHireDate() != null ? Date.valueOf(employee.getHireDate()) : Date.valueOf(LocalDate.now()));
                DAOUtils.setNullableDate(stmt, 7, employee.getDismissalDate() != null ? Date.valueOf(employee.getDismissalDate()) : null);
                stmt.setBoolean(8, employee.getActive() != null ? employee.getActive() : true);
                stmt.setInt(9, employee.getId());
            },
            "Сотрудник успешно обновлен в базе данных",
            "Не удалось обновить сотрудника в базе данных (сотрудник не найден)",
            "при обновлении сотрудника"
        );
    }
    public boolean deleteEmployee(Integer employeeId) {
        if (!validateId(employeeId, "сотрудника")) {
            return false;
        }
        String sql = "DELETE FROM Employees WHERE EmployeeID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, employeeId),
            "Сотрудник успешно удален из базы данных",
            "Не удалось удалить сотрудника из базы данных (сотрудник не найден)",
            "при удалении сотрудника"
        );
    }
    public boolean deleteEmployee(Employee employee) {
        if (employee == null || employee.getId() == null) {
            System.err.println("Нельзя удалить сотрудника без ID");
            return false;
        }
        return deleteEmployee(employee.getId());
    }
}
