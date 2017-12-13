package org.polyjdbc.core.dialect;

/**
 *
 * @author Marvin Diaz
 */
public class DB2400Dialect extends DB2Dialect {

    private DB2400DialectQueries queries = new DB2400DialectQueries();

    @Override
    public String getCode() {
        return DialectRegistry.DB2.name();
    }

    @Override
    public String nextFromSequence(String sequenceName) {
        if (sequenceName.contains(".")) {
            sequenceName = sequenceName.substring(sequenceName.indexOf(".") + 1, sequenceName.length());
        }
        return "values nextval for " + sequenceName ;
    }

    @Override
    public DialectQueries queries() {
        return queries;
    }
}
