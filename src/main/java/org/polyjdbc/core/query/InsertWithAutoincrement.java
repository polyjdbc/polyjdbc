package org.polyjdbc.core.query;

import org.polyjdbc.core.key.KeyGenerator;
import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.type.ColumnTypeMapper;

import java.sql.SQLException;

public class InsertWithAutoincrement extends InsertQuery {

    public InsertWithAutoincrement(ColumnTypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public InsertQuery sequence(String sequenceField, String sequenceName) {
        //just ignore as we don't want to complicate clients' code
        return this;
    }

    @Override
    boolean isSequenceUsed() {
        return false;
    }

    @Override
    long generateSequenceValue(KeyGenerator keyGenerator, Transaction transaction) throws SQLException {
        throw new RuntimeException("not implemented");
    }
}
