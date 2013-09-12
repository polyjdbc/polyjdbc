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

import org.polyjdbc.core.util.StringBuilderUtil;

/**
 *
 * @author Adam Dubiel
 */
public class InsertQuery {

    private static final int VALUES_LENGTH = 50;

    private Query query;

    private StringBuilder valueNames = new StringBuilder(VALUES_LENGTH);

    private StringBuilder values = new StringBuilder(VALUES_LENGTH);

    private String sequenceName;

    InsertQuery() {
        this.query = new Query();
    }

    Query build() {
        StringBuilderUtil.deleteLastCharacters(valueNames, 2);
        StringBuilderUtil.deleteLastCharacters(values, 2);

        query.append("(").append(valueNames.toString()).append(")")
                .append(" VALUES(").append(values.toString()).append(")");
        query.compile();

        return query;
    }

    public InsertQuery into(String tableName) {
        query.append("INSERT INTO ").append(tableName);
        return this;
    }

    public InsertQuery sequence(String sequenceName) {
        this.sequenceName = sequenceName;
        return this;
    }

    String getSequenceName() {
        return sequenceName;
    }

    public InsertQuery value(String fieldName, Object value) {
        valueNames.append(fieldName).append(", ");
        valueNames.append(":").append(fieldName).append(", ");
        query.setArgument(fieldName, value);
        return this;
    }
}
