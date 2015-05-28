package org.polyjdbc.core.query.limit;

import org.polyjdbc.core.query.Query;

public class OracleLimitClauseProvider implements LimitClauseProvider {

    @Override
    public Query limit(Query query, Limit limit) {
        return query.wrap("SELECT a.*, rownum r__ FROM (", ")a WHERE r__ BETWEEN"
                + limit.getOffset() + " AND " + (limit.getOffset() + limit.getLimit()));
    }
}
