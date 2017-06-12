package org.polyjdbc.core.query;

import org.polyjdbc.core.key.KeyGenerator;
import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.type.ColumnTypeMapper;

import java.sql.SQLException;

public class InsertWithAutoincrement extends InsertQuery {

    public InsertWithAutoincrement(ColumnTypeMapper typeMapper) {
        super(typeMapper);
    }
    private boolean isIdInserted = false;

    @Override
    public InsertQuery sequence(String sequenceField, String sequenceName) {
        //pretend that DB has sequences as we don't want to complicate clients' code
        isIdInserted = true;
        return this;
    }

    @Override
    boolean isIdInserted() {
        return isIdInserted;
    }

    @Override
    long generateSequenceValue(KeyGenerator keyGenerator, Transaction transaction) throws SQLException {
        throw new RuntimeException("not implemented");
    }
}
