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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.type.ColumnType;

/**
 *
 * @author Adam Dubiel
 */
public class Query {

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\:[A-Za-z]*");

    private static final String QUERY_PLACEHOLDER = "?";

    private static final int AVERAGE_QUERY_LENGTH = 100;

    private String originalQuery;

    private String query;

    private StringBuilder builder = new StringBuilder(AVERAGE_QUERY_LENGTH);

    private Map<String, Object> arguments = new HashMap<String, Object>();

    private List<Object> orderedArguments = new ArrayList<Object>();

    private boolean compiled = false;

    Query() {
    }

    Query append(String string) {
        builder.append(string);
        return this;
    }

    public Query setArgument(String name, Object value) {
        arguments.put(name, value);
        return this;
    }

    public void clearArguments() {
        query = originalQuery;
        arguments.clear();
        orderedArguments.clear();
        compiled = false;
    }

    void compile() {
        originalQuery = builder.toString();
        query = originalQuery;

        Matcher matcher = ARGUMENT_PATTERN.matcher(query);
        String foundPattern;
        while (matcher.find()) {
            foundPattern = matcher.group();
            orderedArguments.add(arguments.get(foundPattern.replace(":", "")));
        }
        query = matcher.replaceAll(QUERY_PLACEHOLDER);
        compiled = true;
    }

    public void injectValues(PreparedStatement preparedStatement) throws SQLException {
        int index = 1;
        for (Object argument : orderedArguments) {
            preparedStatement.setObject(index, argument, ColumnType.forClass(argument.getClass()).getSqlType());
            index++;
        }
    }

    public PreparedStatement createStatement(Transaction transaction) throws SQLException {
        if (!compiled) {
            compile();
        }
        return transaction.getConnection().prepareStatement(query);
    }

    public PreparedStatement createStatementWithValues(Transaction transaction) throws SQLException {
        PreparedStatement preparedStatement = createStatement(transaction);
        injectValues(preparedStatement);
        return preparedStatement;
    }

    String getQuery() {
        return query;
    }
}
