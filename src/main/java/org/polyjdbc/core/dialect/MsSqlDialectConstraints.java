package org.polyjdbc.core.dialect;

/**
 * @author bartosz.walacik
 */
public class MsSqlDialectConstraints extends DefaultDialectConstraints {

    @Override
    public String createSequence(String name) {
        return "CREATE SEQUENCE " + name + " START WITH 1 INCREMENT BY 1";
    }

    @Override
    public String dropIndex(String name, String targetRelationName) {
        return "DROP INDEX " + name + " ON " + targetRelationName;
    }

    @Override
    String encodeDefaultValue(Object defaultValue) {
        if (defaultValue instanceof Boolean){
            return encodeBooleanToBit( (Boolean)defaultValue );
        }
        return super.encodeDefaultValue(defaultValue);
    }
}
