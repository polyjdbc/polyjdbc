package org.polyjdbc.core;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.transaction.ConnectionProvider;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;
import org.polyjdbc.core.transaction.ExternalTransactionManager;
import org.polyjdbc.core.transaction.TransactionManager;
import org.polyjdbc.core.type.ColumnTypeMapper;
import org.polyjdbc.core.type.Json;
import org.polyjdbc.core.type.SqlType;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public final class PolyJDBCBuilder {

    private final Dialect dialect;

    private String schemaName;

    private final Map<Class<?>, SqlType> customMappings = new HashMap<Class<?>, SqlType>();
    
    private DataSource dataSource;

    private ConnectionProvider connectionProvider;

    private PolyJDBCBuilder(Dialect dialect) {
        this(dialect, null);
    }

    private PolyJDBCBuilder(Dialect dialect, String schemaName) {
        this.dialect = dialect;
        this.schemaName = schemaName;
    }

    /**
     * Start Creating new PolyJDBC instance speaking given dialect.  
     */
    public static PolyJDBCBuilder polyJDBC(Dialect dialect) {
        return new PolyJDBCBuilder(dialect);
    }

    /**
     * Create new PolyJDBC instance with a given dialect and schema.
     */
    public static PolyJDBCBuilder polyJDBC(Dialect dialect, String schemaName) {
        return new PolyJDBCBuilder(dialect, schemaName);
    }

    /**
     * Return new PolyJDBC instance. 
     */
    public PolyJDBC build() {
        TransactionManager manager;
        if (dataSource != null) {
            manager = new DataSourceTransactionManager(dataSource);
        } else {
            manager = new ExternalTransactionManager(connectionProvider);
        }
        setupJsonMapping(customMappings);
        return new DefaultPolyJDBC(dialect, schemaName, new ColumnTypeMapper(customMappings), manager);
    }

    private void setupJsonMapping(Map<Class<?>, SqlType> customMappings) {
        if (dialect.supportsNativeJsonColumnType()) {
            customMappings.put(Json.class, new SqlType(Types.OTHER));
        } else {
            customMappings.put(Json.class, new SqlType(Types.VARCHAR));
        }
    }

    /**
     * Specify source of connections in transactional mode. PolyJDCB will take care of commits, rollbacks and
     * connection closing. 
     */
    public PolyJDBCBuilder connectingToDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    /**
     *  Specify source of connections in unmanaged (passive) mode. PolyJDBC expects provided connections to be managed
     *  by some other entity (ie framework), and so won't attempt to commit, rollback nor close them.
     */
    public PolyJDBCBuilder usingManagedConnections(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        return this;
    }

    /**
     * Register custom mapping from clazz to SQL type. SqlType should be one of {@link java.sql.Types}. If you need to
     * add some transformations, register custom implementation of {@link org.polyjdbc.core.type.TypeWrapper}.
     *  
     * @param clazz class to register
     * @param sqlType type compatible with {@link java.sql.Types}
     */
    public PolyJDBCBuilder withCustomMapping(Class<?> clazz, SqlType sqlType) {
        this.customMappings.put(clazz, sqlType);
        return this;
    }
}
