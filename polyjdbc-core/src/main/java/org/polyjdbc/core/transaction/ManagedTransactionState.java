package org.polyjdbc.core.transaction;

import org.polyjdbc.core.exception.PolyJdbcException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class ManagedTransactionState implements TransactionState {

    private final Connection connection;

    private final List<Statement> statements = new ArrayList<Statement>();

    private final List<ResultSet> resultSets = new ArrayList<ResultSet>();

    ManagedTransactionState(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void registerStatement(Statement statement) {
        statements.add(0, statement);
    }

    @Override
    public void registerCursor(ResultSet resultSet) {
        resultSets.add(0, resultSet);
    }

    @Override
    public void commit() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.commit();
            }
        } catch (SQLException exception) {
            throw new PolyJdbcException("TRANSACTION_COMMIT_ERROR", "Failed to commit transaction transaction.", exception);
        }
    }

    @Override
    public void rollback() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException exception) {
            throw new PolyJdbcException("TRANSACTION_ROLLBACK_ERROR", "Failed to rollback transaction.", exception);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && connection.isClosed()) {
                throw new PolyJdbcException("CLOSING_CLOSED_CONNECTION", "Tried to close already closed connection! " +
                        "Check for some unwanted close() in your code.");
            }

            for (ResultSet resultSet : resultSets) {
                resultSet.close();
            }
            for (Statement statement : statements) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException exception) {
            throw new PolyJdbcException("TRANSACTION_CLOSE_ERROR", "Failed to close transaction.", exception);
        }
    }
}
