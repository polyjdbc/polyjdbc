package org.polyjdbc.core.dialect;

/**
 *
 * @author Marvin Diaz
 */
public class DB2DialectTypes extends DefaultDialectTypes {

    @Override
    public String text() {
        return "CLOB";
    }

    @Override
    public String number(int integerPrecision, int decimalPrecision) {
        return "DECIMAL(" + integerPrecision + "," + decimalPrecision + ")";
    }

    @Override
    public String floatType() {
        return "DECFLOAT";
    }

    @Override
    public String bool() {
        return "CHAR(1)";
    }

    @Override
    public String json() {
        return "BLOB";
    }

}
