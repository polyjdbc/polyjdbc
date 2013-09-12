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
import org.polyjdbc.core.query.DDLQuery;
import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.query.TransactionalQueryRunner;
import org.polyjdbc.core.query.loader.ClasspathQueryLoader;
import org.polyjdbc.core.query.loader.QueryLoader;
import org.polyjdbc.core.transaction.TransactionManager;

/**
 *
 * @author Adam Dubiel
 */
public class SchemaManager {

    private Dialect dialect;

    private QueryLoader queryLoader = new ClasspathQueryLoader();

    public SchemaManager(Dialect dialect) {
        this.dialect = dialect;
    }

    public void createSchema(TransactionManager transactionManager) {
        executeDDL(transactionManager, "/ddl/" + dialect.getCode().toLowerCase() + "_ddl.sql");
    }

    public void dropSchema(TransactionManager transactionManager) {
        executeDDL(transactionManager, "/ddl/" + dialect.getCode().toLowerCase() + "_ddl_drop.sql");
    }

    private void executeDDL(TransactionManager transactionManager, String resourceName) {
        DDLQuery query = queryLoader.getQuery(resourceName);
        QueryRunner runner = new TransactionalQueryRunner(transactionManager.openTransaction());
        runner.ddl(query);
        runner.commitAndClose();
    }

}
