package org.polyjdbc.core.query.limit;

import org.polyjdbc.core.query.Query;

public interface LimitClauseProvider {

    Query limit(Query query, LimitAndOffset offset);
}
