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

import org.polyjdbc.core.dialect.Dialect;

/**
 *
 * @author Adam Dubiel
 */
public class Sequence implements SchemaEntity {

    private final String name;

    private final Dialect dialect;

    private String schemaNameWithSeparator;

    public Sequence(Dialect dialect, String name) {
        this(dialect, name, "");
    }

    public Sequence(Dialect dialect, String name, String schemaName) {
        this.name = name;
        this.dialect = dialect;
        if (schemaName == null || schemaName.isEmpty()) {
            this.schemaNameWithSeparator = "";
        } else {
            this.schemaNameWithSeparator = schemaName + ".";
        }
    }

    @Override
    public String toString() {
        return ddl();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String ddl() {
        return dialect.constraints().createSequence(schemaNameWithSeparator+name);
    }

    @Override
    public String dropDDL() {
        return "DROP SEQUENCE " + schemaNameWithSeparator+name;
    }

    public static final class Builder {

        private final Sequence sequence;

        private final Schema schema;

        private Builder(Schema schema, String name) {
            this.schema = schema;
            this.sequence = new Sequence(schema.getDialect(), name);
        }

        private Builder(Schema schema, String name, String schemaName) {
            this.schema = schema;
            this.sequence = new Sequence(schema.getDialect(), name, schemaName);
        }

        public static Builder sequence(Schema schema, String name) {
            return new Builder(schema, name);
        }

        public static Builder sequence(Schema schema, String name, String schemaName) {
            return new Builder(schema, name, schemaName);
        }

        public Sequence build() {
            if (schema != null) {
                schema.add(sequence);
            }
            return sequence;
        }

    }

}
