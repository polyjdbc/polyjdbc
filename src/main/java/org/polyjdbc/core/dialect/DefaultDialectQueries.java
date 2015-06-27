package org.polyjdbc.core.dialect;

import org.polyjdbc.core.query.Query;

public class DefaultDialectQueries implements DialectQueries {

    @Override
    public void limit(Query query, Limit limit, boolean isOrdered) {
        query.append(" LIMIT " + limit.getLimit() + " OFFSET " + limit.getOffset());
    }
}
