package org.polyjdbc.core.dialect;

import org.polyjdbc.core.query.Query;

/**
 *
 * @author Marvin Diaz
 */
public class DB2400DialectQueries implements DialectQueries {

    @Override
    public void limit(Query query, Limit limit, boolean isOrdered) {
        if(limit.getOffset() > 0) {
            System.err.println("DB2400DialectQueries.limit: Offset is not supported");
        }
        query.append(" FETCH FIRST " + limit.getLimit() + " ROWS ONLY ");
    }
}
