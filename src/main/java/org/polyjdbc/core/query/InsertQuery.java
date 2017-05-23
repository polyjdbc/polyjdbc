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

import org.polyjdbc.core.key.KeyGenerator;
import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.type.ColumnTypeMapper;
import org.polyjdbc.core.util.StringBuilderUtil;

import java.sql.SQLException;

/**
 * Builds insert query, use {@link QueryFactory#insert() } to create new instance.
 *
 * <pre>
 * QueryFactory.insert().into("test").sequence("id", "seq_test")
 *      .value("columnA", "A")
 *      .value("columnB", 2)
 *      .value("columnC", BigDecimal.valueOf(3.1415));
 * </pre>
 *
 * @author Adam Dubiel
 */
public abstract class InsertQuery {

    private static final int VALUES_LENGTH = 50;

    private final Query query;

    private final StringBuilder valueNames = new StringBuilder(VALUES_LENGTH);

    private final StringBuilder values = new StringBuilder(VALUES_LENGTH);

    InsertQuery(ColumnTypeMapper typeMapper) {
        this.query = new Query(typeMapper);
    }

    Query build() {
        StringBuilderUtil.deleteLastCharacters(valueNames, 2);
        StringBuilderUtil.deleteLastCharacters(values, 2);

        query.append("(").append(valueNames.toString()).append(")")
                .append(" VALUES(").append(values.toString()).append(")");
        query.compile();

        return query;
    }

    /**
     * Creates <b>INSERT INTO</b> clause for given table name.
     */
    public InsertQuery into(String tableName) {
        query.append("INSERT INTO ").append(tableName);
        return this;
    }

    /**
     * Insert next sequence value into column of given name. Only one sequenced
     * column per table is supported so far.
     */
    public abstract InsertQuery sequence(String sequenceField, String sequenceName);

    abstract boolean isSequenceUsed();

    abstract long generateSequenceValue(KeyGenerator keyGenerator, Transaction transaction) throws SQLException;

    /**
     * Insert value into column of given name. Object is automatically translated
     * onto matching JDBC type.
     *
     * @see org.polyjdbc.core.type.ColumnTypeMapper
     */
    public InsertQuery value(String fieldName, Object value) {
        valueNames.append(fieldName).append(", ");
        values.append(":").append(fieldName).append(", ");
        setArgument(fieldName, value);
        return this;
    }

    void setArgument(String fieldName, Object value) {
        query.setArgument(fieldName, value);
    }
}
