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
package org.polyjdbc.core.integration;

import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.model.Schema;

public class TestSchemaManager {

    private final PolyJDBC polyJDBC;

    private Schema schema;

    private String schemaName;

    public TestSchemaManager(PolyJDBC polyJDBC, String schemaName) {
        this.polyJDBC = polyJDBC;
        this.schemaName = schemaName;
    }

    public void createSchema() {
        if (schemaName == null) {
            schema = new Schema(polyJDBC.dialect());
        } else {
            schema = new Schema(polyJDBC.dialect(), schemaName);
        }

        schema.addRelation("test")
                .withAttribute().longAttr("id").withAdditionalModifiers("AUTO_INCREMENT").and()
                .withAttribute().string("name").unique().notNull().withMaxLength(200).and()
                .withAttribute().string("pseudo").withMaxLength(200).and()
                .withAttribute().integer("some_count").and()
                .withAttribute().booleanAttr("countable").withDefaultValue(true).notNull().and()
                .withAttribute().character("separator_char").withDefaultValue(';').notNull().and()
                .constrainedBy().primaryKey("pk_test").using("id").and()
                .build();
        schema.addIndex("idx_test_pseudo").on("test").indexing("pseudo").build();
        schema.addSequence("seq_test").build();

        schema.addRelation("type_test")
                .withAttribute().string("code").withMaxLength(40).unique().notNull().and()
                .withAttribute().longAttr("long_attr").withDefaultValue(0).and()
                .withAttribute().string("string_attr").withMaxLength(40).and()
                .withAttribute().integer("integer_attr").withDefaultValue(0).and()
                .withAttribute().floatAttr("float_attr").withDefaultValue(0).and()
                .withAttribute().number("number_attr").withIntegerPrecision(5).withDecimalPrecision(2).withDefaultValue(0).and()
                .withAttribute().booleanAttr("boolean_attr").withDefaultValue(false).and()
                .withAttribute().character("character_attr").withDefaultValue('0').and()
                .withAttribute().text("text_attr").and()
                .withAttribute().date("date_attr").and()
                .withAttribute().timestamp("timestamp_attr").and()
                .constrainedBy().primaryKey("pk_type_test").using("code").and()
                .build();

        SchemaManager manager = null;
        try {
            manager = polyJDBC.schemaManager();
            manager.create(schema);
        } finally {
            polyJDBC.close(manager);
        }
    }

    public void dropSchema() {
        SchemaManager manager = null;
        try {
            manager = polyJDBC.schemaManager();
            manager.drop(schema);
        } finally {
            polyJDBC.close(manager);
        }
    }
}
