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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.polyjdbc.core.exception.SchemaManagerException;
import org.polyjdbc.core.schema.model.Relation;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.schema.model.SchemaPart;
import org.polyjdbc.core.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Adam Dubiel
 */
public class SchemaManagerImpl implements SchemaManager {

    private static final Logger logger = LoggerFactory.getLogger(SchemaManagerImpl.class);

    private Transaction transaction;

    public SchemaManagerImpl(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void create(Schema schema) {
        String ddlText;
        for(SchemaPart entity : schema.getEntities()) {
            ddlText = entity.ddl();
            logger.info("creating entity with name {} using ddl:\n{}", entity.getName(), ddlText);
            ddl(DDLQuery.ddl(ddlText));
        }
    }

    @Override
    public void create(Relation relation) {
        ddl(DDLQuery.ddl(relation.toString()));
    }

    @Override
    public void ddl(DDLQuery ddlQuery) {
        String textQuery = ddlQuery.build();
        try {
            PreparedStatement statement = transaction.getConnection().prepareStatement(textQuery);
            transaction.registerPrepareStatement(statement);
            transaction.execute(statement);
        } catch (SQLException exception) {
            transaction.rollback();
            throw new SchemaManagerException("DDL_ERROR", String.format("Failed to run delete query:%n%s", textQuery), exception);
        }
    }

    @Override
    public void close() {
        transaction.closeWithArtifacts();
    }
}
