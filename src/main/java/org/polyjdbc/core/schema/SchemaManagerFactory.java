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
package org.polyjdbc.core.schema;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.transaction.TransactionManager;

/**
 *
 * @author Adam Dubiel
 */
public class SchemaManagerFactory {

    private final TransactionManager transactionManager;

    private String schemaName;

    private final Dialect dialect;

    public SchemaManagerFactory(TransactionManager transactionManager, String schemaName, Dialect dialect) {
        this.transactionManager = transactionManager;
        this.dialect = dialect;
        if (schemaName == null || schemaName.isEmpty()) {
            this.schemaName = null;
        } else {
            this.schemaName = schemaName;
        }
    }

    public SchemaInspector createInspector() {
        return new SchemaInspectorImpl(transactionManager.openTransaction(), schemaName, dialect);
    }

    public SchemaManager createManager() {
        return new SchemaManagerImpl(transactionManager.openTransaction());
    }
}
