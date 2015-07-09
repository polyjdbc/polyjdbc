package org.polyjdbc.core.dialect;

import org.polyjdbc.core.exception.QueryExecutionException;
import org.polyjdbc.core.query.Query;

public class MsSqlDialectQueries implements DialectQueries {
    @Override
    public void limit(Query query, Limit limit, boolean isOrdered) {

        if (limit.isEmpty()){
            return;
        }

        if (isOrdered){
            query.append(" OFFSET " + limit.getOffset() + " ROWS FETCH NEXT " + limit.getLimit() + " ROWS ONLY");
        }
        else{
            if (limit.getOffset() > 0){
                throw new QueryExecutionException("LIMIT_ERROR",
                    "Failed to run select query: "+query.getQuery()+", MS SQL doesn't support OFFSET without ORDER BY clause");
            }
            query.wrap("select TOP "+limit.getLimit()+" * from (",") a");
        }
    }
}
