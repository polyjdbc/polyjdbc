package org.polyjdbc.core;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.transaction.ConnectionProvider;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;
import org.polyjdbc.core.transaction.ExternalTransactionManager;
import org.polyjdbc.core.transaction.TransactionManager;

import javax.sql.DataSource;

public final class PolyJDBCBuilder {

    private final Dialect dialect;

    private DataSource dataSource;

    private ConnectionProvider connectionProvider;

    private PolyJDBCBuilder(Dialect dialect) {
        this.dialect = dialect;
    }

    public static PolyJDBCBuilder polyJDBC(Dialect dialect) {
        return new PolyJDBCBuilder(dialect);
    }

    public PolyJDBC build() {
        TransactionManager manager;
        if (dataSource != null) {
            manager = new DataSourceTransactionManager(dataSource);
        } else {
            manager = new ExternalTransactionManager(connectionProvider);
        }
        return new DefaultPolyJDBC(dialect, manager);
    }

    public PolyJDBCBuilder connectingToDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public PolyJDBCBuilder usingManagedConnections(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        return this;
    }
}
