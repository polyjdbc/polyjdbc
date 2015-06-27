package org.polyjdbc.core.dialect;

import org.polyjdbc.core.query.Query;

public class MsSqlDialectQueries implements DialectQueries {
    @Override
    public void limit(Query query, Limit limit) {

        if (limit.isEmpty()){
            return;
        }

        if (limit.getOffset() == 0){
            query.wrap("select TOP "+limit.getLimit()+" * from (",") a");
        } else {
            query.append(" OFFSET " + limit.getOffset() + " ROWS FETCH NEXT " + limit.getLimit() + " ROWS ONLY");
        }
    }
}
