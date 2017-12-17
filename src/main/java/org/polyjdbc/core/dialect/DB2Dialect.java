package org.polyjdbc.core.dialect;

/**
 *
 * @author Marvin Diaz
 */
public class DB2Dialect extends AbstractDialect {

    private DialectTypes types = new DB2DialectTypes();

    @Override
    public String getCode() {
        return DialectRegistry.DB2.name();
    }

    @Override
    public String nextFromSequence(String sequenceName) {
        return "values nextval for " + sequenceName ;
    }

    @Override
    public DialectTypes types() {
        return types;
    }
}
