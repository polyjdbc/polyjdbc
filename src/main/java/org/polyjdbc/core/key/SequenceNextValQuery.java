package org.polyjdbc.core.key;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.transaction.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author bartosz.walacik
 */
class SequenceNextValQuery {
    private final Dialect dialect;

    SequenceNextValQuery(Dialect dialect) {
        this.dialect = dialect;
    }

    long queryForNextVal(String sequenceName, Transaction transaction) throws SQLException {
        try(PreparedStatement statement = transaction.prepareStatement(dialect.nextFromSequence(sequenceName));
            ResultSet resultSet = statement.executeQuery())
        {
            transaction.registerCursor(resultSet);
            resultSet.next();
            return resultSet.getLong(1);
        }
    }
}
