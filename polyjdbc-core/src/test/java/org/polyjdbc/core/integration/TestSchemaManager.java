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

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.SchemaManagerImpl;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.transaction.TransactionManager;

/**
 *
 * @author Adam Dubiel
 */
public class TestSchemaManager {

    private Dialect dialect;

    private Schema schema;

    public TestSchemaManager(Dialect dialect) {
        this.dialect = dialect;
    }

    public void createSchema(TransactionManager transactionManager) {
        schema = new Schema(dialect);

        schema.addRelation("test")
                .withAttribute().longAttr("id").and()
                .withAttribute().string("name").unique().notNull().withMaxLength(200).and()
                .withAttribute().integer("count").and()
                .withAttribute().booleanAttr("countable").withDefaultValue(true).notNull().and()
                .withAttribute().character("separator").withDefaultValue(';').notNull().and()
                .constrainedBy().primaryKey("pk").using("id").and()
                .build();
        schema.addIndex("idx_test_name").on("test").indexing("name").build();
        schema.addSequence("seq_test").build();

        SchemaManager manager = new SchemaManagerImpl(transactionManager.openTransaction());
        manager.create(schema);
        manager.close();
    }

    public void dropSchema(TransactionManager transactionManager) {
        SchemaManager manager = new SchemaManagerImpl(transactionManager.openTransaction());
        manager.drop(schema);
        manager.close();
    }
}
