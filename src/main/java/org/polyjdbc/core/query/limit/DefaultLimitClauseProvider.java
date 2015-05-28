package org.polyjdbc.core.query.limit;

import org.polyjdbc.core.query.Query;

public class DefaultLimitClauseProvider implements LimitClauseProvider {
    @Override
    public Query limit(Query query, Limit limit) {
        return query.append(" LIMIT " + limit.getLimit() + " OFFSET " + limit.getOffset());
    }
}
