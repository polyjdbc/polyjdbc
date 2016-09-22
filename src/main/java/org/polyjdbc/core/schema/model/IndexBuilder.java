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
public final class IndexBuilder {

    private Index index;

    private Schema schema;

    private IndexBuilder(Dialect dialect, String name) {
        this.index = new Index(dialect, name);
    }

    private IndexBuilder(Schema schema, String name) {
        this(schema.getDialect(), name);
        this.schema = schema;
    }

    private IndexBuilder(Dialect dialect, String name, String schemaName) {
        this.index = new Index(dialect, name, schemaName);
    }

    private IndexBuilder(Schema schema, String name, String schemaName) {
        this(schema.getDialect(), name, schemaName);
        this.schema = schema;
    }

    public static IndexBuilder index(Dialect dialect, String name) {
        return new IndexBuilder(dialect, name);
    }

    public static IndexBuilder index(Schema schema, String name) {
        return new IndexBuilder(schema, name);
    }

    public static IndexBuilder index(Dialect dialect, String name, String schemaName) {
        return new IndexBuilder(dialect, name, schemaName);
    }

    public static IndexBuilder index(Schema schema, String name, String schemaName) {
        return new IndexBuilder(schema, name, schemaName);
    }

    public Index build() {
        if (schema != null) {
            schema.add(index);
        }
        return index;
    }

    public IndexBuilder on(String relation) {
        index.withTargetRelation(relation);
        return this;
    }

    public IndexBuilder indexing(String... attributes) {
        index.withTargetAttributes(attributes);
        return this;
    }
}
