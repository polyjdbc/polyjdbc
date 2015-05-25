package org.polyjdbc.core.query.limit;

public class DefaultLimitClauseProvider implements LimitClauseProvider {
    @Override
    public String limit(int limit, int offset) {
        return " LIMIT " + limit + " OFFSET " + offset;
    }
}
