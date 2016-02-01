package org.polyjdbc.core.key;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;

public class KeyGeneratorFactory {

    public static KeyGenerator create(Dialect dialect) {
        switch (DialectRegistry.valueOf(dialect.getCode())) {
            case H2:
                return new SequenceAllocation(dialect);
            case POSTGRES:
                return new SequenceAllocation(dialect);
            case MYSQL:
                return new AutoIncremented();
            case ORACLE:
                return new SequenceAllocation(dialect);
            case MSSQL:
                return new SequenceAllocation(dialect);
            default:
                throw new IllegalStateException("Cannot create key generator for unknown dialect: " + dialect.getCode());
        }
    }

}
