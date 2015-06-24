package org.polyjdbc.core.dialect;

import org.polyjdbc.core.query.Query;

public class OracleDialectQueries implements DialectQueries {
    @Override
    public void limit(Query query, Limit limit) {
        query.wrap("SELECT a.*, rownum r__ FROM (", ")a WHERE rownum BETWEEN "
                + limit.getOffset() + " AND " + (limit.getOffset() + limit.getLimit()));
    }
}
