package org.polyjdbc.core.dialect;

import org.polyjdbc.core.query.Query;

public interface DialectQueries {

    void limit(Query query, Limit limit);
}
