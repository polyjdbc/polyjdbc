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
import org.polyjdbc.core.dialect.MsSqlDialect;
import org.polyjdbc.core.exception.SchemaInspectionException;
import org.polyjdbc.core.transaction.Transaction;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

class SchemaInspectorImpl implements SchemaInspector {

    private Locale locale;

    private final Transaction transaction;

    private DatabaseMetaData metadata;

    private String catalog;

    private String schemaName;

    private final Dialect dialect;

    SchemaInspectorImpl(Transaction transaction) {
        this(transaction, Locale.ENGLISH);
    }

    SchemaInspectorImpl(Transaction transaction, Locale locale) {
        this(transaction, locale, null, null);
    }

    SchemaInspectorImpl(Transaction transaction, String schemaName, Dialect dialect) {
        this(transaction, Locale.ENGLISH, schemaName, dialect);
    }

    SchemaInspectorImpl(Transaction transaction, Locale locale, String schemaName, Dialect dialect) {
        this.transaction = transaction;
        this.locale = locale;
        this.schemaName = schemaName;
        this.dialect = dialect;
        extractMetadata(transaction);
    }

    private void extractMetadata(Transaction transaction) {
        try {
            Connection connection = transaction.getConnection();
            metadata = connection.getMetaData();
            catalog = connection.getCatalog();
        } catch (SQLException exception) {
            throw new SchemaInspectionException("METADATA_EXTRACTION_ERROR", "Failed to obtain metadata from connection.", exception);
        }
    }

    @Override
    public boolean relationExists(String name) {
        try {
            ResultSet resultSet = metadata.getTables(catalog, schemaName, convertCase(name), new String[]{"TABLE"});
            transaction.registerCursor(resultSet);

            if (schemaName != null) {
                return resultSet.next();
            } else {
                while (resultSet.next()) {
                    String tableSchemaName = resultSet.getString("TABLE_SCHEM");
                    if ( tableSchemaName == null ||
                         tableSchemaName.equalsIgnoreCase("public") ||
                         tableSchemaName.equals("") ||
                        (dialect instanceof MsSqlDialect && tableSchemaName.equalsIgnoreCase("dbo"))
                    ) {
                        return true;
                    }
                }
                return false;
            }
        } catch (SQLException exception) {
            throw new SchemaInspectionException("RELATION_LOOKUP_ERROR", "Failed to obtain relation list when looking for relation " + name, exception);
        }
    }

    @Override
    public boolean indexExists(String relationName, String indexName) {
        try {
            ResultSet resultSet = metadata.getIndexInfo(catalog, schemaName, convertCase(relationName), false, true);
            transaction.registerCursor(resultSet);

            String normalizedIndexName = convertCase(indexName);
            while(resultSet.next()) {
                String rsetIndexName = resultSet.getString("INDEX_NAME");
                if(rsetIndexName !=null && rsetIndexName.equals(normalizedIndexName)) {
                    return true;
                }
            }
            
            return false;
        } catch (SQLException exception) {
            throw new SchemaInspectionException("INDEX_LOOKUP_ERROR", "Failed to obtain relation index list when looking for indexes for relation " + relationName, exception);
        }
    }

    private String convertCase(String identifier) throws SQLException {
        if(metadata.storesLowerCaseIdentifiers()) {
            return identifier.toLowerCase(locale);
        }
        if(metadata.storesUpperCaseIdentifiers()) {
            return identifier.toUpperCase(locale);
        }

        return identifier;
    }

    @Override
    public void close() {
        transaction.commit();
        transaction.close();
    }
}
