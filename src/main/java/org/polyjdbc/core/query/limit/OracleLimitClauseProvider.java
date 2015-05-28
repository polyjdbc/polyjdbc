package org.polyjdbc.core.query.limit;

import org.polyjdbc.core.query.Query;

public class OracleLimitClauseProvider implements LimitClauseProvider {
    @Override
    public Query limit(Query query, LimitAndOffset offset) {


        return query;
    }
}
