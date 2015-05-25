package org.polyjdbc.core.query.limit;

public interface LimitClauseProvider {

    String limit(int limit, int offset);
}
