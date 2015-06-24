package org.polyjdbc.core.dialect;

/**
 * @author bartosz.walacik
 */
public class OracleDialectConstraints extends DefaultDialectConstraints {

    @Override
    public String attributeModifiers(boolean unique, boolean notNull, Object defaultValue) {
        StringBuilder builder = new StringBuilder();

        if (unique) {
            builder.append("UNIQUE ");
        }
        if (defaultValue != null) {
            appendAttrDefaultValue(builder, defaultValue);
        }
        if (notNull) {
            builder.append("NOT NULL ");
        }

        return builder.toString();
    }

    @Override
    String encodeDefaultValue(Object defaultValue) {
        if (defaultValue instanceof Boolean){
            Boolean defaultBoolean = (Boolean)defaultValue;
            if (defaultBoolean){
                return "1";
            }
            else{
                return "0";
            }
        }
        return super.encodeDefaultValue(defaultValue);
    }
}
