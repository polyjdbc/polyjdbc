package org.polyjdbc.core.query.limit;

public class Limit {

    private final int limit;
    private final int offset;

    public Limit(int limit, int offset) {
        this.limit = limit;
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }
}
