package org.polyjdbc.core.transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

interface TransactionState {

    void registerStatement(Statement statement);
    
    void registerCursor(ResultSet resultSet);

    void commit();
    
    void rollback();

    void close();

}
