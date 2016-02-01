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

import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.PolyJDBCBuilder;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;
import org.polyjdbc.core.query.QueryFactory;
import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;
import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.transaction.TransactionManager;
import org.polyjdbc.core.util.TheCloser;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

public abstract class PolyDatabaseTest {

    private DataSource dataSource;

    private PolyJDBC polyJDBC;

    private TransactionManager transactionManager;

    private TheCleaner cleaner;

    protected PolyJDBC polyJDBC() {
        return polyJDBC;
    }

    protected Dialect dialect() {
        return polyJDBC.dialect();
    }

    protected QueryFactory query() {
        return polyJDBC.query();
    }

    protected DataSource dataSource() {
        return dataSource;
    }

    protected QueryRunner queryRunner() {
        return polyJDBC.queryRunner();
    }

    protected Transaction transaction() {
        return transactionManager.openTransaction();
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
        Dialect dialect = DialectRegistry.dialect(dialectCode);

        this.dataSource = DataSourceFactory.create(dialect, jdbcUrl, user, password);
        this.polyJDBC = PolyJDBCBuilder.polyJDBC(dialect).connectingToDataSource(dataSource).build();
        this.transactionManager = new DataSourceTransactionManager(dataSource);

        this.cleaner = new TheCleaner(polyJDBC);

        return dataSource;
    }

    protected void deleteFromRelations(List<String> relations) {
        cleaner.cleanDB(relations);
    }

    protected void dropDatabase() throws Exception {
        dropSchema();
    }

    protected abstract void dropSchema();

}
