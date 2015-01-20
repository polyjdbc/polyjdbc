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

import java.io.Closeable;
import java.sql.*;

public class Transaction implements Closeable {

    private final Connection connection;

    private final TransactionState transactionState;

    Transaction(Connection connection, TransactionState transactionState) {
        this.connection = connection;
        this.transactionState = transactionState;
    }

    public Connection getConnection() {
        return connection;
    }

    public int executeUpdate(PreparedStatement preparedStatement) throws SQLException {
        try {
            registerStatement(preparedStatement);
            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            transactionState.rollback();
            throw exception;
        }
    }

    public boolean execute(PreparedStatement preparedStatement) throws SQLException {
        try {
            registerStatement(preparedStatement);
            return preparedStatement.execute();
        } catch (SQLException exception) {
            transactionState.rollback();
            throw exception;
        }
    }

    public ResultSet executeQuery(PreparedStatement preparedStatement) throws SQLException {
        try {
            registerStatement(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            registerCursor(resultSet);
            return resultSet;
        } catch (SQLException exception) {
            transactionState.rollback();
            throw exception;
        }
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        try {
            return connection.prepareStatement(query);
        } catch (SQLException exception) {
            transactionState.rollback();
            throw exception;
        }
    }

    public Statement createStatement() throws SQLException {
        try {
            Statement statement = connection.createStatement();
            registerStatement(statement);
            return statement;
        } catch (SQLException exception) {
            transactionState.rollback();
            throw exception;
        }
    }

    public void registerStatement(Statement statement) {
        transactionState.registerStatement(statement);
    }

    public void registerCursor(ResultSet resultSet) {
        transactionState.registerCursor(resultSet);
    }

    public void commit() {
        transactionState.commit();
    }

    public void rollback() {
        transactionState.rollback();
    }

    @Override
    public void close() {
        transactionState.close();
    }
}
