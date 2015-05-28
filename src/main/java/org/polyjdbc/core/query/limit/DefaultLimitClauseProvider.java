package org.polyjdbc.core.query.limit;

import org.polyjdbc.core.query.Query;

public class DefaultLimitClauseProvider implements LimitClauseProvider {
    @Override
    public Query limit(Query query, LimitAndOffset limitAndOffset) {
        return query.append(" LIMIT " + limitAndOffset.getLimit() + " OFFSET " + limitAndOffset.getOffset());
    }
}
