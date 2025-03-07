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

import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.type.ColumnTypeMapper;
import org.polyjdbc.core.type.Json;
import org.polyjdbc.core.type.SqlType;
import org.polyjdbc.core.type.TypeWrapper;
import org.polyjdbc.core.util.StringBuilderUtil;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {

    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("\\:[A-Za-z0-9_-]*");

    private static final String QUERY_PLACEHOLDER = "?";

    private static final int AVERAGE_QUERY_LENGTH = 100;

    private static final int ARGUMENT_REPLACEMENT_SIZE = 10;

    private final ColumnTypeMapper typeMapper;

    private String originalQuery;

    private String query;

    private StringBuilder builder = new StringBuilder(AVERAGE_QUERY_LENGTH);

    private final Map<String, Object> arguments = new HashMap<String, Object>();

    private final List<Object> orderedArguments = new ArrayList<Object>();

    private boolean compiled = false;

    Query(ColumnTypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public Query append(String string) {
        builder.append(string);
        return this;
    }

    Query overwrite(String string) {
        builder = new StringBuilder(AVERAGE_QUERY_LENGTH);
        builder.append(string);
        return this;
    }

    public Query wrap(String prefix, String sufix) {
        builder.insert(0, prefix).append(sufix);
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
        String foundPattern, argumentName, replacement;
        while (matcher.find()) {
            foundPattern = matcher.group();
            argumentName = foundPattern.substring(1);
            orderedArguments.add(arguments.get(argumentName));

            replacement = createReplacement(arguments.get(argumentName));
            query = query.replaceFirst(foundPattern, replacement);
        }
        compiled = true;
    }

    private String createReplacement(Object argument) {
        if (isCollection(argument)) {
            Iterator<?> iterator = createCollectionIterator(argument);

            StringBuilder replacementBuilder = new StringBuilder(ARGUMENT_REPLACEMENT_SIZE);
            if (!iterator.hasNext()) {
                return "";
            }

            while (iterator.hasNext()) {
                replacementBuilder.append(QUERY_PLACEHOLDER).append(", ");
                iterator.next();
            }

            StringBuilderUtil.deleteLastCharacters(replacementBuilder, 2);
            return replacementBuilder.toString();
        }
        if (argument instanceof Json) {
            return ((Json) argument).cast(QUERY_PLACEHOLDER);
        }
        return QUERY_PLACEHOLDER;
    }

    public void injectValues(PreparedStatement preparedStatement) throws SQLException {
        int argumentNumber = 1;
        for (Object argument : orderedArguments) {
            if (isCollection(argument)) {
                Iterator<?> iterator = createCollectionIterator(argument);
                while (iterator.hasNext()) {
                    injectValue(preparedStatement, argumentNumber, iterator.next());
                    argumentNumber++;
                }
            } else {
                injectValue(preparedStatement, argumentNumber, argument);
                argumentNumber++;
            }
        }
    }

    private void injectValue(PreparedStatement preparedStatement, int argumentNumber, Object value) throws SQLException {
        if (value != null) {
            SqlType type = typeMapper.forClass(value.getClass());
            Object injectedValue = value;
            if (value instanceof TypeWrapper) {
                injectedValue = ((TypeWrapper) value).value();
            } else
            if (value instanceof java.util.Date) {
                //Oracle is unhappy with java.util.Date and insists on java.sql.Date
                injectedValue = new java.sql.Date(((java.util.Date)value).getTime());
            } else
            if (value instanceof Character){
                //Oracle really dislike Java char type
                injectedValue = String.valueOf(value);
            } else
            if (value instanceof Boolean){
               //Oracle. Why U No Boolean?
               //surprisingly preparedStatement.setBoolean(,) works with Oracle and translates boolean to 0/1
               //but preparedStatement.setObject(,,BOOLEAN) doesn't
               preparedStatement.setBoolean(argumentNumber, (Boolean)value);
               return;
            }

            preparedStatement.setObject(argumentNumber, injectedValue, type.code());
        } else {
            preparedStatement.setObject(argumentNumber, null);
        }
    }

    private boolean isCollection(Object object) {
        return object != null && (Iterable.class.isAssignableFrom(object.getClass()) || object.getClass().isArray());
    }

    private Iterator<?> createCollectionIterator(Object argument) {
        Iterator<?> iterator;
        if (argument.getClass().isArray()) {
            iterator = Arrays.asList((Object[]) argument).iterator();
        } else {
            iterator = ((Iterable<?>) argument).iterator();
        }
        return iterator;
    }

    public PreparedStatement createStatement(Transaction transaction) throws SQLException {
        if (!compiled) {
            compile();
        }
        return transaction.prepareStatement(query);
    }

    public PreparedStatement createStatementWithValues(Transaction transaction) throws SQLException {
        PreparedStatement preparedStatement = createStatement(transaction);
        injectValues(preparedStatement);
        return preparedStatement;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return originalQuery;
    }
}
