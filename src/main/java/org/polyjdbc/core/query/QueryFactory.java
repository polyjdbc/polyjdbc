/*
 * Copyright 2014 Adam Dubiel.
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

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.query.limit.LimitClauseProvider;
import org.polyjdbc.core.type.ColumnTypeMapper;

public class QueryFactory {

    private final Dialect dialect;

    private final ColumnTypeMapper typeMapper;
    private final LimitClauseProvider limitClauseProvider;

    public QueryFactory(Dialect dialect, ColumnTypeMapper typeMapper, LimitClauseProvider limitClauseProvider) {
        this.dialect = dialect;
        this.typeMapper = typeMapper;
        this.limitClauseProvider = limitClauseProvider;
    }

    /**
     * Create insert query.
     */
    public InsertQuery insert() {
        return new InsertQuery(typeMapper);
    }

    /**
     * Create select query, specifying the <b>select</b> clause.
     * <pre>QueryFactory.select("columnA, columnB");</pre>
     */
    public SelectQuery select(String what) {
        return new SelectQuery(dialect, typeMapper, limitClauseProvider, what);
    }

    /**
     * Create select query which selects all columns, equivalent to
     * <code>QueryFactory.select("*")</code>.
     */
    public SelectQuery selectAll() {
        return new SelectQuery(dialect, typeMapper, limitClauseProvider, "*");
    }

    /**
     * Create empty select statement, can only be used with
     * {@link SelectQuery#query(java.lang.String)} and must contain all clauses
     * except from order by and limit.
     * <pre>
     * QueryFactory.select().query("select * from test where column > :column").orderBy("column", Order.ASC)
     * .withArgument("column", 2);
     * </pre>
     */
    public SelectQuery select() {
        return new SelectQuery(dialect, typeMapper, limitClauseProvider);
    }

    /**
     * Create update query on given table.
     */
    public UpdateQuery update(String what) {
        return new UpdateQuery(typeMapper, what);
    }

    /**
     * Create delete query.
     */
    public DeleteQuery delete() {
        return new DeleteQuery(typeMapper);
    }
}
