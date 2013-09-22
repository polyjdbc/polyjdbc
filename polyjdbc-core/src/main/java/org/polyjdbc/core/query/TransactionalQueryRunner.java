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
package org.polyjdbc.core.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.polyjdbc.core.exception.NonUniqueException;
import org.polyjdbc.core.exception.QueryExecutionException;
import org.polyjdbc.core.key.KeyGenerator;
import org.polyjdbc.core.mapper.EmptyMapper;
import org.polyjdbc.core.mapper.ObjectMapper;
import org.polyjdbc.core.transaction.Transaction;

/**
 *
 * @author Adam Dubiel
 */
public class TransactionalQueryRunner implements QueryRunner {

    private static final EmptyMapper EMPTY_MAPPER = new EmptyMapper();

    private Transaction transaction;

    public TransactionalQueryRunner(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public <T> T queryUnique(SelectQuery query, ObjectMapper<T> mapper) {
        return queryUnique(query, mapper, true);
    }

    @Override
    public <T> T queryUnique(SelectQuery query, ObjectMapper<T> mapper, boolean failOnNotUniqueOrNotFound) {
        Query rawQuery = query.build();
        List<T> results = queryList(rawQuery, mapper);

        if (results.size() != 1) {
            if (failOnNotUniqueOrNotFound) {
                if (results.isEmpty()) {
                    throw new NonUniqueException("NO_ITEM_FOUND", String.format("Asked for unique result but no items found for query:%n%s", rawQuery.getQuery()));
                } else {
                    throw new NonUniqueException("NON_UNIQUE_ITEM", String.format("Asked for unique result but %d items found for query:%n%s", results.size(), rawQuery.getQuery()));
                }
            } else {
                return null;
            }
        }
        return results.get(0);
    }

    @Override
    public <T> List<T> queryList(SelectQuery query, ObjectMapper<T> mapper) {
        return queryList(query.build(), mapper);
    }

    private <T> List<T> queryList(Query query, ObjectMapper<T> mapper) {
        try {
            PreparedStatement statement = query.createStatementWithValues(transaction);
            ResultSet resultSet = transaction.executeQuery(statement);

            List<T> results = new ArrayList<T>();
            while (resultSet.next()) {
                results.add(mapper.createObject(resultSet));
            }
            return results;
        } catch (SQLException exception) {
            transaction.rollback();
            throw new QueryExecutionException("SELECT_ERROR", String.format("Failed to run select query:%n%s", query.getQuery()), exception);
        }
    }

    @Override
    public boolean queryExistence(SelectQuery query) {
        return !queryList(query, EMPTY_MAPPER).isEmpty();
    }

    @Override
    public long insert(InsertQuery insertQuery) {
        try {
            KeyGenerator keyGenerator = transaction.dialectKeyGenerator();
            long key = keyGenerator.generateKey(insertQuery.getSequenceName(), transaction);
            insertQuery.sequenceValue(key);

            Query rawQuery = insertQuery.build();
            PreparedStatement statement = rawQuery.createStatementWithValues(transaction);
            transaction.executeUpdate(statement);

            return keyGenerator.getKeyFromLastInsert(transaction);
        } catch (SQLException exception) {
            transaction.rollback();
            Query rawQuery = insertQuery.build();
            throw new QueryExecutionException("INSERT_ERROR", String.format("Failed to run insert query:%n%s", rawQuery), exception);
        }
    }

    @Override
    public int delete(DeleteQuery deleteQuery) {
        Query rawQuery = deleteQuery.build();
        try {
            PreparedStatement statement = rawQuery.createStatementWithValues(transaction);
            return transaction.executeUpdate(statement);
        } catch (SQLException exception) {
            transaction.rollback();
            throw new QueryExecutionException("DELETE_ERROR", String.format("Failed to run delete query:%n%s", rawQuery.getQuery()), exception);
        }
    }

    @Override
    public void ddl(DDLQuery ddlQuery) {
        Query rawQuery = ddlQuery.build();
        try {
            PreparedStatement statement = rawQuery.createStatementWithValues(transaction);
            transaction.executeUpdate(statement);
        } catch (SQLException exception) {
            transaction.rollback();
            throw new QueryExecutionException("DDL_ERROR", String.format("Failed to run delete query:%n%s", rawQuery.getQuery()), exception);
        }
    }

    public void commitAndClose() {
        transaction.commit();
        transaction.closeWithArtifacts();
    }

    public void rollback() {
        transaction.rollback();
    }

    public void close() {
        transaction.closeWithArtifacts();
    }
}
