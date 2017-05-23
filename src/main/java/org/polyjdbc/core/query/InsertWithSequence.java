package org.polyjdbc.core.query;

import org.polyjdbc.core.key.KeyGenerator;
import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.type.ColumnTypeMapper;

import java.sql.SQLException;

class InsertWithSequence extends InsertQuery {
    private String sequenceField;
    private String sequenceName;

    InsertWithSequence(ColumnTypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public InsertQuery sequence(String sequenceField, String sequenceName) {
        this.sequenceField = sequenceField;
        this.sequenceName = sequenceName;
        return value(sequenceField, sequenceField);
    }

    @Override
    boolean isIdInserted() {
        return sequenceName != null;
    }

    @Override
    long generateSequenceValue(KeyGenerator keyGenerator, Transaction transaction) throws SQLException {
        long key = keyGenerator.generateKey(sequenceName, transaction);
        setArgument(sequenceField, key);
        return key;
    }
}
