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

import javax.sql.DataSource;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;
import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.query.QueryRunnerFactory;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;
import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.transaction.TransactionManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 *
 * @author Adam Dubiel
 */
public class DatabaseTest {

    private TransactionManager transactionManager;

    private QueryRunnerFactory queryRunnerFactory;

    private TestSchemaManager schemaManager;

    private TheCleaner cleaner;

    protected Transaction transaction() {
        return transactionManager.openTransaction();
    }

    protected QueryRunner queryRunner() {
        return queryRunnerFactory.create();
    }

    @Parameters({"dialect", "url", "user", "password"})
    @BeforeClass(alwaysRun = true)
    public void setUpDatabase(@Optional("H2") String dialectCode, @Optional("jdbc:h2:mem:test") String url, @Optional("polly") String user, @Optional("polly") String password) throws Exception {
        Dialect dialect = DialectRegistry.dialect(dialectCode);
        DataSource dataSource = DataSourceFactory.create(dialect, url, user, password);

        this.transactionManager = new DataSourceTransactionManager(dataSource);
        this.queryRunnerFactory = new QueryRunnerFactory(dialect, transactionManager);
        this.schemaManager = new TestSchemaManager(dialect);
        this.cleaner = new TheCleaner(queryRunnerFactory);

        schemaManager.createSchema(transactionManager);
    }

    @BeforeMethod(alwaysRun = true)
    public void cleanDatabase() {
        cleaner.cleanDB("test");
    }

    @AfterClass(alwaysRun = true)
    public void tearDownDatabase() throws Exception {
        schemaManager.dropSchema(transactionManager);
    }
}
