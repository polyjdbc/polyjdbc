/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.polyjdbc.core.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.exception.PolyJDBCException;
import org.polyjdbc.core.key.KeyGenerator;

/**
 *
 * @author Adam Dubiel
 */
public class Transaction {

    private Dialect dialect;

    private Connection connection;

    private List<PreparedStatement> preparedStatements = new ArrayList<PreparedStatement>();

    private List<ResultSet> resultSets = new ArrayList<ResultSet>();

    public Transaction(Dialect dialect, Connection connection) {
        this.dialect = dialect;
        this.connection = connection;
    }

    public Dialect getDialect() {
        return dialect;
    }

    public Connection getConnection() {
        return connection;
    }

    public int executeUpdate(PreparedStatement preparedStatement) throws SQLException {
        try {
            registerPrepareStatement(preparedStatement);
            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            rollback();
            throw exception;
        }
    }

    public ResultSet executeQuery(PreparedStatement preparedStatement) throws SQLException {
        try {
            registerPrepareStatement(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            registerCursor(resultSet);
            return resultSet;
        } catch (SQLException exception) {
            rollback();
            throw exception;
        }
    }

    public KeyGenerator dialectKeyGenerator() {
        return dialect.keyGenerator();
    }

    public void registerPrepareStatement(PreparedStatement preparedStatement) {
        preparedStatements.add(0, preparedStatement);
    }

    public void registerCursor(ResultSet resultSet) {
        resultSets.add(0, resultSet);
    }

    public void commit() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.commit();
            }
        } catch (SQLException exception) {
            throw new PolyJDBCException("TRANSACTION_COMMIT_ERROR", "Failed to commit transaction transaction.", exception);
        }
    }

    public void rollback() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException exception) {
            throw new PolyJDBCException("TRANSACTION_ROLLBACK_ERROR", "Failed to rollback transaction.", exception);
        }
    }

    public void closeWithArtifacts() {
        try {
            for (ResultSet resultSet : resultSets) {
                resultSet.close();
            }
            for (PreparedStatement statement : preparedStatements) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException exception) {
            throw new PolyJDBCException("TRANSACTION_CLOSE_ERROR", "Failed to close transaction.", exception);
        }
    }
}