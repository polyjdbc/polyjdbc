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

import java.sql.DatabaseMetaData;

/**
 *
 * @author Adam Dubiel
 */
class ConnectionMetadata {

    private DatabaseMetaData databaseMetaData;

    private String catalog;

    private String schema;

    ConnectionMetadata(DatabaseMetaData databaseMetaData, String catalog, String schema) {
        this.databaseMetaData = databaseMetaData;
        this.catalog = catalog;
        this.schema = schema;
    }

    DatabaseMetaData getDatabaseMetaData() {
        return databaseMetaData;
    }

    String getCatalog() {
        return catalog;
    }

    String getSchema() {
        return schema;
    }
}
