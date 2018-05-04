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
package org.polyjdbc.core.schema.model;

import java.util.Arrays;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.util.StringUtils;

/**
 *
 * @author Adam Dubiel
 */
public class Index implements SchemaEntity {

    private static final int DDL_LENGTH = 50;

    private Dialect dialect;

    private String name;

    private String targetRelation;

    private String[] targetAttributes;

    private String schemaNameWithSeparator;

    Index(Dialect dialect, String name) {
        this(dialect, name, "");
    }

    Index(Dialect dialect, String name, String schemaName) {
        this.dialect = dialect;
        this.name = name;
        if ((schemaName == null) || (schemaName.isEmpty())) {
            this.schemaNameWithSeparator = "";
        } else {
            this.schemaNameWithSeparator = schemaName + ".";
        }
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ddl();
    }

    @Override
    public String ddl() {
        StringBuilder builder = new StringBuilder(DDL_LENGTH);
        builder.append("CREATE INDEX ").append(name).append(" ON ").append(schemaNameWithSeparator).append(targetRelation).append("(")
                .append(StringUtils.concatenate(",", (Object[]) targetAttributes)).append(")");
        return builder.toString();
    }

    @Override
    public String dropDDL() {
        return dialect.constraints().dropIndex(name, schemaNameWithSeparator+targetRelation);
    }

    public String getTargetRelation() {
        return targetRelation;
    }

    void withTargetRelation(String targetRelation) {
        this.targetRelation = targetRelation;
    }

    public String[] getTargetAttributes() {
        return Arrays.copyOf(targetAttributes, targetAttributes.length);
    }

    void withTargetAttributes(String[] targetAttributes) {
        this.targetAttributes = Arrays.copyOf(targetAttributes, targetAttributes.length);
    }

    public static final class Builder {

        private Index index;

        private Schema schema;

        private Builder(Dialect dialect, String name) {
            this.index = new Index(dialect, name);
        }

        private Builder(Schema schema, String name) {
            this(schema.getDialect(), name);
            this.schema = schema;
        }

        private Builder(Dialect dialect, String name, String schemaName) {
            this.index = new Index(dialect, name, schemaName);
        }

        private Builder(Schema schema, String name, String schemaName) {
            this(schema.getDialect(), name, schemaName);
            this.schema = schema;
        }

        public static Builder index(Dialect dialect, String name) {
            return new Builder(dialect, name);
        }

        public static Builder index(Schema schema, String name) {
            return new Builder(schema, name);
        }

        public static Builder index(Dialect dialect, String name, String schemaName) {
            return new Builder(dialect, name, schemaName);
        }

        public static Builder index(Schema schema, String name, String schemaName) {
            return new Builder(schema, name, schemaName);
        }

        public Index build() {
            if (schema != null) {
                schema.add(index);
            }
            return index;
        }

        public Builder on(String relation) {
            index.withTargetRelation(relation);
            return this;
        }

        public Builder indexing(String... attributes) {
            index.withTargetAttributes(attributes);
            return this;
        }

    }

}
