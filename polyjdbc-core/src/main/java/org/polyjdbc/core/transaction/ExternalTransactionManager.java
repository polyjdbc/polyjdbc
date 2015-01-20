package org.polyjdbc.core.transaction;

import org.polyjdbc.core.exception.PolyJdbcException;

import java.sql.SQLException;

public class ExternalTransactionManager implements TransactionManager {
    
    private final ConnectionProvider transactionProvider;

    public ExternalTransactionManager(ConnectionProvider transactionProvider) {
        this.transactionProvider = transactionProvider;
    }

    @Override
    public Transaction openTransaction() {
        try {
            return new Transaction(transactionProvider.getConnection(), new ExternalTransactionState());
        } catch (SQLException e) {
            throw new PolyJdbcException("OPEN_CONNECTION_ERROR", "Failed to obtain connection from connection provider.", e);
        }
    }

    @Override
    public Transaction openTransaction(boolean autoCommit) {
        return openTransaction();
    }
}
