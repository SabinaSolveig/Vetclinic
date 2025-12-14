package org.example.vetclinic.database;
import org.example.vetclinic.model.Appointment;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
public class AppointmentDAO extends BaseDAO {
    public List<Appointment> getAllAppointments() {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.AppointmentID, a.ClientID, a.PetID, a.EmployeeID, " +
                     "a.AppointmentDate, a.AppointmentTime, a.Status, a.Notes, " +
                     "c.LastName || ' ' || c.FirstName as ClientName, " +
                     "p.Name as PetName, " +
                     "e.LastName || ' ' || e.FirstName as EmployeeName " +
                     "FROM Appointments a " +
                     "INNER JOIN Clients c ON a.ClientID = c.ClientID " +
                     "INNER JOIN Pets p ON a.PetID = p.PetID " +
                     "INNER JOIN Employees e ON a.EmployeeID = e.EmployeeID " +
                     "ORDER BY a.AppointmentDate DESC, a.AppointmentTime DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("AppointmentID");
                Integer clientId = rs.getInt("ClientID");
                Integer petId = rs.getInt("PetID");
                Integer employeeId = rs.getInt("EmployeeID");
                LocalDate appointmentDate = null;
                Date date = rs.getDate("AppointmentDate");
                if (date != null) {
                    appointmentDate = date.toLocalDate();
                }
                LocalTime appointmentTime = null;
                Time time = rs.getTime("AppointmentTime");
                if (time != null) {
                    appointmentTime = time.toLocalTime();
                }
                String status = rs.getString("Status");
                String notes = rs.getString("Notes");
                String clientName = rs.getString("ClientName");
                String petName = rs.getString("PetName");
                String employeeName = rs.getString("EmployeeName");
                Appointment appointment = new Appointment(id, clientId, petId, employeeId,
                                                         appointmentDate, appointmentTime, status, notes,
                                                         clientName, petName, employeeName);
                list.add(appointment);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке заявок на прием");
        }
        return list;
    }
    public List<Appointment> getAppointmentsWithFilters(Integer clientId, Integer employeeId, String status, String sortField, boolean ascending) {
        List<Appointment> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT a.AppointmentID, a.ClientID, a.PetID, a.EmployeeID, " +
            "a.AppointmentDate, a.AppointmentTime, a.Status, a.Notes, " +
            "c.LastName || ' ' || c.FirstName as ClientName, " +
            "p.Name as PetName, " +
            "e.LastName || ' ' || e.FirstName as EmployeeName " +
            "FROM Appointments a " +
            "INNER JOIN Clients c ON a.ClientID = c.ClientID " +
            "INNER JOIN Pets p ON a.PetID = p.PetID " +
            "INNER JOIN Employees e ON a.EmployeeID = e.EmployeeID " +
            "WHERE 1=1"
        );
        List<Object> parameters = new ArrayList<>();
        if (clientId != null) {
            sql.append(" AND a.ClientID = ?");
            parameters.add(clientId);
        }
        if (employeeId != null) {
            sql.append(" AND a.EmployeeID = ?");
            parameters.add(employeeId);
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND a.Status = CAST(? AS appointment_status)");
            parameters.add(status);
        }
        if (sortField != null && !sortField.isEmpty()) {
            sql.append(" ORDER BY ").append(sortField);
            if (ascending) {
                sql.append(" ASC");
            } else {
                sql.append(" DESC");
            }
            if (!sortField.equals("a.AppointmentDate")) {
                sql.append(", a.AppointmentDate DESC");
            }
        } else {
            sql.append(" ORDER BY a.AppointmentDate DESC, a.AppointmentTime DESC");
        }
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                }
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("AppointmentID");
                Integer cId = rs.getInt("ClientID");
                Integer petId = rs.getInt("PetID");
                Integer empId = rs.getInt("EmployeeID");
                LocalDate appointmentDate = null;
                Date date = rs.getDate("AppointmentDate");
                if (date != null) {
                    appointmentDate = date.toLocalDate();
                }
                LocalTime appointmentTime = null;
                Time time = rs.getTime("AppointmentTime");
                if (time != null) {
                    appointmentTime = time.toLocalTime();
                }
                String appointmentStatus = rs.getString("Status");
                String notes = rs.getString("Notes");
                String clientName = rs.getString("ClientName");
                String petName = rs.getString("PetName");
                String employeeName = rs.getString("EmployeeName");
                Appointment appointment = new Appointment(id, cId, petId, empId,
                                                         appointmentDate, appointmentTime, appointmentStatus, notes,
                                                         clientName, petName, employeeName);
                list.add(appointment);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке заявок на прием");
        }
        return list;
    }
    public boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO Appointments (ClientID, PetID, EmployeeID, AppointmentDate, AppointmentTime, Status, Notes) " +
                     "VALUES (?, ?, ?, ?, ?, CAST(? AS appointment_status), ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, appointment.getClientId());
                stmt.setInt(2, appointment.getPetId());
                stmt.setInt(3, appointment.getEmployeeId());
                stmt.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
                stmt.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
                String status = appointment.getStatus() != null ? appointment.getStatus() : "Scheduled";
                if (!status.equals("Scheduled") && !status.equals("Completed") && 
                    !status.equals("Cancelled") && !status.equals("NoShow")) {
                    status = "Scheduled";
                }
                stmt.setString(6, status);
                DAOUtils.setNullableString(stmt, 7, appointment.getNotes());
            },
            "Заявка на прием успешно добавлена в базу данных",
            "Не удалось добавить заявку на прием в базу данных",
            "при добавлении заявки на прием"
        );
    }
    public boolean updateAppointment(Appointment appointment) {
        if (!validateId(appointment.getId(), "заявку на прием")) {
            return false;
        }
        String sql = "UPDATE Appointments SET ClientID = ?, PetID = ?, EmployeeID = ?, " +
                     "AppointmentDate = ?, AppointmentTime = ?, Status = CAST(? AS appointment_status), Notes = ? WHERE AppointmentID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, appointment.getClientId());
                stmt.setInt(2, appointment.getPetId());
                stmt.setInt(3, appointment.getEmployeeId());
                stmt.setDate(4, Date.valueOf(appointment.getAppointmentDate()));
                stmt.setTime(5, Time.valueOf(appointment.getAppointmentTime()));
                String status = appointment.getStatus() != null ? appointment.getStatus() : "Scheduled";
                if (!status.equals("Scheduled") && !status.equals("Completed") && 
                    !status.equals("Cancelled") && !status.equals("NoShow")) {
                    status = "Scheduled";
                }
                stmt.setString(6, status);
                DAOUtils.setNullableString(stmt, 7, appointment.getNotes());
                stmt.setInt(8, appointment.getId());
            },
            "Заявка на прием успешно обновлена в базе данных",
            "Не удалось обновить заявку на прием в базе данных (заявка не найдена)",
            "при обновлении заявки на прием"
        );
    }
    public boolean deleteAppointment(Integer appointmentId) {
        if (!validateId(appointmentId, "заявку на прием")) {
            return false;
        }
        String sql = "DELETE FROM Appointments WHERE AppointmentID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, appointmentId),
            "Заявка на прием успешно удалена из базы данных",
            "Не удалось удалить заявку на прием из базы данных (заявка не найдена)",
            "при удалении заявки на прием"
        );
    }
    public boolean deleteAppointment(Appointment appointment) {
        if (appointment == null || appointment.getId() == null) {
            System.err.println("Нельзя удалить заявку на прием без ID");
            return false;
        }
        return deleteAppointment(appointment.getId());
    }
}
