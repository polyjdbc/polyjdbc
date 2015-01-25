package org.polyjdbc.core;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.transaction.ConnectionProvider;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;
import org.polyjdbc.core.transaction.ExternalTransactionManager;
import org.polyjdbc.core.transaction.TransactionManager;
import org.polyjdbc.core.type.ColumnTypeMapper;
import org.polyjdbc.core.type.SqlType;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public final class PolyJDBCBuilder {

    private final Dialect dialect;

    private final Map<Class<?>, SqlType> customMappings = new HashMap<Class<?>, SqlType>();
    
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
        return new DefaultPolyJDBC(dialect, new ColumnTypeMapper(customMappings), manager);
    }

    public PolyJDBCBuilder connectingToDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public PolyJDBCBuilder usingManagedConnections(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        return this;
    }
    
    public PolyJDBCBuilder withCustomMapping(Class<?> clazz, SqlType sqlType) {
        this.customMappings.put(clazz, sqlType);
        return this;
    }
}
