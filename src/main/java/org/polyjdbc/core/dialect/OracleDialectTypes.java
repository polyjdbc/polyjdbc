package org.polyjdbc.core.dialect;

/**
 *
 * @author Adam Dubiel
 */
public class OracleDialectTypes extends DefaultDialectTypes {

    @Override
    public String bool() {
        return "NUMBER(1)";
    }

    @Override
    public String bigint(int integerPrecision) {
        return "NUMBER";
    }

    @Override
    public String integer(int integerPrecision) {
        return "NUMBER";
    }

    @Override
    public String string(int characters) {
        return "VARCHAR2(" + characters + ")";
    }

    @Override
    public String number(int integerPrecision, int decimalPrecision) {
        return "NUMBER(" + integerPrecision + "," + decimalPrecision + ")";
    }

    @Override
    public String floatType() {
        return "REAL";
    }
}
