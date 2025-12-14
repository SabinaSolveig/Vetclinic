package org.example.vetclinic.database;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
public class DAOUtils {
    public static void setNullableString(PreparedStatement stmt, int parameterIndex, String value) throws SQLException {
        if (value != null && !value.isEmpty()) {
            stmt.setString(parameterIndex, value);
        } else {
            stmt.setNull(parameterIndex, Types.VARCHAR);
        }
    }
    public static void setNullableInteger(PreparedStatement stmt, int parameterIndex, Integer value) throws SQLException {
        if (value != null) {
            stmt.setInt(parameterIndex, value);
        } else {
            stmt.setNull(parameterIndex, Types.INTEGER);
        }
    }
    public static void setNullableDate(PreparedStatement stmt, int parameterIndex, java.sql.Date value) throws SQLException {
        if (value != null) {
            stmt.setDate(parameterIndex, value);
        } else {
            stmt.setNull(parameterIndex, Types.DATE);
        }
    }
    public static void handleSQLException(SQLException e, String operation) {
        System.err.println("Ошибка " + operation + " из базы данных: " + e.getMessage());
        e.printStackTrace();
    }
    public static boolean checkUpdateResult(int rowsAffected, String successMessage, String errorMessage) {
        if (rowsAffected > 0) {
            System.out.println(successMessage);
            return true;
        } else {
            System.err.println(errorMessage);
            return false;
        }
    }
    public static boolean validateId(Integer id, String entityName) {
        if (id == null) {
            System.err.println("Нельзя обновить/удалить " + entityName + " без ID");
            return false;
        }
        return true;
    }
    public static Integer getNullableInteger(java.sql.ResultSet rs, String columnName) throws SQLException {
        if (rs.getObject(columnName) != null) {
            return rs.getInt(columnName);
        }
        return null;
    }
}
