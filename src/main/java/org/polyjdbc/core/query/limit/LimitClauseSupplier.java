package org.polyjdbc.core.query.limit;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.OracleDialect;

public class LimitClauseSupplier {

    public LimitClauseProvider supply(Dialect dialect) {
        if (dialect instanceof OracleDialect) {
            return new OracleLimitClauseProvider();
        }

        return new DefaultLimitClauseProvider();
    }
}
