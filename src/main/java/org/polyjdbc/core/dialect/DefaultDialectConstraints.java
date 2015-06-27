/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.polyjdbc.core.dialect;

import org.polyjdbc.core.util.StringUtils;
import org.polyjdbc.core.util.TypeUtil;

/**
 *
 * @author Adam Dubiel
 */
public class DefaultDialectConstraints implements DialectConstraints {

    @Override
    public String createSequence(String name) {
        return "CREATE SEQUENCE " + name;
    }

    @Override
    public String primaryKey(String name, String[] targetAttributes) {
        return "CONSTRAINT " + name + " PRIMARY KEY(" + StringUtils.concatenate(", ", (Object[]) targetAttributes) + ")";
    }

    @Override
    public String foreignKey(String name, String targetAttribute, String targetRelation, String targetRelationAttribute) {
        return "CONSTRAINT " + name + " FOREIGN KEY(" + targetAttribute + ") REFERENCES " + targetRelation + "(" + targetRelationAttribute + ")";
    }

    @Override
    public String dropIndex(String name, String targetRelationName) {
        return "DROP INDEX " + name;
    }

    @Override
    public String attributeModifiers(boolean unique, boolean notNull, Object defaultValue) {
        StringBuilder builder = new StringBuilder();

        if (unique) {
            builder.append("UNIQUE ");
        }
        if (notNull) {
            builder.append("NOT NULL ");
        }
        if (defaultValue != null) {
            appendAttrDefaultValue(builder, defaultValue);
        }

        return builder.toString();
    }

    void appendAttrDefaultValue(StringBuilder builder, Object defaultValue) {
        boolean primitive = TypeUtil.isNonQuotablePrimitive(defaultValue);

        builder.append("DEFAULT ");
        if (!primitive) {
            builder.append("'");
        }
        builder.append(encodeDefaultValue(defaultValue));
        if (!primitive) {
            builder.append("'");
        }
        builder.append(" ");
    }

    String encodeDefaultValue(Object defaultValue){
        return defaultValue.toString();
    }

    String encodeBooleanToBit(Boolean bool){
        if (bool){
            return "1";
        }
        else{
            return "0";
        }
    }
}
