/*
 * Copyright 2014 Adam Dubiel, Przemek Hertel.
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
package org.polyjdbc.core.infrastructure;

import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;
import org.polyjdbc.core.key.KeyGeneratorRegistry;
import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.query.QueryRunnerFactory;
import org.polyjdbc.core.schema.SchemaManagerFactory;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;
import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.transaction.TransactionManager;
import org.polyjdbc.core.util.TheCloser;

/**
 *
 * @author Adam Dubiel
 */
public abstract class PolyDatabaseTest {

    private Dialect dialect;

    private TransactionManager transactionManager;

    private QueryRunnerFactory queryRunnerFactory;

    private SchemaManagerFactory schemaManagerFactory;

    private TheCleaner cleaner;

    protected Dialect dialect() {
        return dialect;
    }

    protected QueryRunnerFactory queryRunnerFactory() {
        return queryRunnerFactory;
    }

    protected QueryRunner queryRunner() {
        return queryRunnerFactory.create();
    }

    protected Transaction transaction() {
        return transactionManager.openTransaction();
    }

    protected SchemaManagerFactory schemaManagerFactory() {
        return schemaManagerFactory;
    }

    protected TransactionManager transactionManager() {
        return transactionManager;
    }

    protected void h2DatabaseInterface() {
        Transaction transaction = null;
        try {
            transaction = transactionManager.openTransaction();
            org.h2.tools.Server.startWebServer(transaction.getConnection());
        } catch (SQLException exception) {
            throw new IllegalStateException(exception);
        } finally {
            TheCloser.close(transaction);
        }
    }

    protected DataSource createDatabase(String dialectCode, String jdbcUrl, String user, String password) throws Exception {
        dialect = DialectRegistry.dialect(dialectCode);

        DataSource dataSource = DataSourceFactory.create(dialect, jdbcUrl, user, password);
        this.transactionManager = new DataSourceTransactionManager(dataSource);
        this.queryRunnerFactory = new QueryRunnerFactory(dialect, transactionManager);
        this.schemaManagerFactory = new SchemaManagerFactory(transactionManager);

        this.cleaner = new TheCleaner(queryRunnerFactory);
        
        return dataSource;
    }

    protected void cleanDatabase() {
        cleaner.cleanDB(entitiesToClean());
    }

    protected abstract List<String> entitiesToClean();

    protected void dropDatabase() throws Exception {
        dropSchema();
        KeyGeneratorRegistry.keyGenerator(dialect).reset();
    }

    protected abstract void dropSchema();

}
