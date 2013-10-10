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
public class SelectQuery {

    private static final int ORDER_BY_LENGTH = 20;

    private Query query;

    private StringBuilder orderBy;

    SelectQuery() {
        this.query = new Query();
    }

    Query build() {
        if (orderBy != null) {
            StringBuilderUtil.deleteLastCharacters(orderBy, 2);
            query.append(orderBy.toString());
        }
        query.compile();
        return query;
    }

    public SelectQuery query(String queryText) {
        query.append(queryText);
        return this;
    }

    public SelectQuery append(String queryText) {
        query.append(queryText);
        return this;
    }

    public SelectQuery orderBy(String name, Order order) {
        if (orderBy == null) {
            orderBy = new StringBuilder(ORDER_BY_LENGTH);
            orderBy.append(" order by ");
        }
        orderBy.append(name).append(" ").append(order.getStringCode()).append(", ");

        return this;
    }

    public SelectQuery withArgument(String argumentName, Object object) {
        query.setArgument(argumentName, object);
        return this;
    }
}
