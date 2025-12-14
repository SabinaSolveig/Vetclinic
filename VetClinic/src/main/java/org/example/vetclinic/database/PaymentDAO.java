package org.example.vetclinic.database;
import org.example.vetclinic.model.Payment;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class PaymentDAO extends BaseDAO {
    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String sql = "SELECT p.PaymentID, p.VisitID, p.PaymentDate, p.Amount, p.PaymentMethodID, " +
                     "p.Status, p.Notes, " +
                     "v.VisitDate || ' ' || COALESCE(v.StartTime::text, '') as VisitInfo, " +
                     "COALESCE(pm.PaymentMethodName, '') as PaymentMethodName " +
                     "FROM Payments p " +
                     "INNER JOIN Visits v ON p.VisitID = v.VisitID " +
                     "LEFT JOIN PaymentMethods pm ON p.PaymentMethodID = pm.PaymentMethodID " +
                     "ORDER BY p.PaymentDate DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Integer id = rs.getInt("PaymentID");
                Integer visitId = rs.getInt("VisitID");
                LocalDateTime paymentDate = null;
                Timestamp timestamp = rs.getTimestamp("PaymentDate");
                if (timestamp != null) {
                    paymentDate = timestamp.toLocalDateTime();
                }
                BigDecimal amount = rs.getBigDecimal("Amount");
                Integer paymentMethodId = DAOUtils.getNullableInteger(rs, "PaymentMethodID");
                String status = rs.getString("Status");
                String notes = rs.getString("Notes");
                String visitInfo = rs.getString("VisitInfo");
                String paymentMethodName = rs.getString("PaymentMethodName");
                Payment payment = new Payment(id, visitId, paymentDate, amount, paymentMethodId, status, notes,
                                            visitInfo, paymentMethodName);
                list.add(payment);
            }
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, "при загрузке оплат");
        }
        return list;
    }
    public boolean addPayment(Payment payment) {
        String sql = "INSERT INTO Payments (VisitID, PaymentDate, Amount, PaymentMethodID, Status, Notes) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, payment.getVisitId());
                stmt.setTimestamp(2, payment.getPaymentDate() != null ? Timestamp.valueOf(payment.getPaymentDate()) : Timestamp.valueOf(LocalDateTime.now()));
                stmt.setBigDecimal(3, payment.getAmount());
                DAOUtils.setNullableInteger(stmt, 4, payment.getPaymentMethodId());
                stmt.setString(5, payment.getStatus() != null ? payment.getStatus() : "Pending");
                DAOUtils.setNullableString(stmt, 6, payment.getNotes());
            },
            "Оплата успешно добавлена в базу данных",
            "Не удалось добавить оплату в базу данных",
            "при добавлении оплаты"
        );
    }
    public boolean updatePayment(Payment payment) {
        if (!validateId(payment.getId(), "оплату")) {
            return false;
        }
        String sql = "UPDATE Payments SET VisitID = ?, PaymentDate = ?, Amount = ?, PaymentMethodID = ?, Status = ?, Notes = ? WHERE PaymentID = ?";
        return executeUpdate(
            sql,
            stmt -> {
                stmt.setInt(1, payment.getVisitId());
                stmt.setTimestamp(2, payment.getPaymentDate() != null ? Timestamp.valueOf(payment.getPaymentDate()) : Timestamp.valueOf(LocalDateTime.now()));
                stmt.setBigDecimal(3, payment.getAmount());
                DAOUtils.setNullableInteger(stmt, 4, payment.getPaymentMethodId());
                stmt.setString(5, payment.getStatus() != null ? payment.getStatus() : "Pending");
                DAOUtils.setNullableString(stmt, 6, payment.getNotes());
                stmt.setInt(7, payment.getId());
            },
            "Оплата успешно обновлена в базе данных",
            "Не удалось обновить оплату в базе данных (оплата не найдена)",
            "при обновлении оплаты"
        );
    }
    public boolean deletePayment(Integer paymentId) {
        if (!validateId(paymentId, "оплату")) {
            return false;
        }
        String sql = "DELETE FROM Payments WHERE PaymentID = ?";
        return executeUpdate(
            sql,
            stmt -> stmt.setInt(1, paymentId),
            "Оплата успешно удалена из базы данных",
            "Не удалось удалить оплату из базы данных (оплата не найдена)",
            "при удалении оплаты"
        );
    }
    public boolean deletePayment(Payment payment) {
        if (payment == null || payment.getId() == null) {
            System.err.println("Нельзя удалить оплату без ID");
            return false;
        }
        return deletePayment(payment.getId());
    }
}
