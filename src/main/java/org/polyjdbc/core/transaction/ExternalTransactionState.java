package org.polyjdbc.core.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.Statement;

public class ExternalTransactionState implements TransactionState {

    private static final Logger logger = LoggerFactory.getLogger(ExternalTransactionState.class);
    
    @Override
    public void registerStatement(Statement statement) {
        logger.trace("Register statement on unmanaged ExternalTransaction, ignoring.");
    }

    @Override
    public void registerCursor(ResultSet resultSet) {
        logger.trace("Register cursor called on unmanaged ExternalTransaction, ignoring.");
    }

    @Override
    public void commit() {
        logger.trace("Commit called on unmanaged ExternalTransaction, ignoring.");
    }

    @Override
    public void rollback() {
        logger.trace("Rollback called on unmanaged ExternalTransaction, ignoring.");
    }

    @Override
    public void close() {
        logger.trace("Clode called on unmanaged ExternalTransaction, ignoring.");
    }
}
