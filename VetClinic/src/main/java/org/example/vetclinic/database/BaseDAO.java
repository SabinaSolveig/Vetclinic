package org.example.vetclinic.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public abstract class BaseDAO {
    protected boolean executeUpdate(String sql, 
                                   ParameterSetter parameterSetter,
                                   String successMessage, 
                                   String errorMessage,
                                   String operationMessage) {
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (parameterSetter != null) {
                parameterSetter.setParameters(stmt);
            }
            int rowsAffected = stmt.executeUpdate();
            return DAOUtils.checkUpdateResult(rowsAffected, successMessage, errorMessage);
        } catch (SQLException e) {
            DAOUtils.handleSQLException(e, operationMessage);
            return false;
        }
    }
    protected boolean validateId(Integer id, String entityName) {
        return DAOUtils.validateId(id, entityName);
    }
    @FunctionalInterface
    protected interface ParameterSetter {
        void setParameters(PreparedStatement stmt) throws SQLException;
    }
}
