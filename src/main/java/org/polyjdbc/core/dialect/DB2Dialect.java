package org.polyjdbc.core.dialect;

/**
 *
 * @author Marvin Diaz
 */
public class DB2Dialect extends AbstractDialect {

    private DialectConstraints constraints = new DefaultDialectConstraints();

    @Override
    public String getCode() {
        return DialectRegistry.DB2.name();
    }

    @Override
    public String nextFromSequence(String sequenceName) {
        return "values nextval for " + sequenceName ;
    }

    @Override
    public DialectConstraints constraints() {
        return constraints;
    }

}
