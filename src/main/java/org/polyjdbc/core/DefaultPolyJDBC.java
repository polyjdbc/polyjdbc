package org.polyjdbc.core;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.key.KeyGeneratorFactory;
import org.polyjdbc.core.query.*;
import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.SchemaManagerFactory;
import org.polyjdbc.core.transaction.TransactionManager;
import org.polyjdbc.core.type.ColumnTypeMapper;
import org.polyjdbc.core.util.TheCloser;

import java.io.Closeable;

public class DefaultPolyJDBC implements PolyJDBC {

    private final Dialect dialect;

    private final QueryFactory queryFactory;

    private final QueryRunnerFactory queryRunnerFactory;

    private final SimpleQueryRunner simpleQueryRunner;

    private final TransactionRunner transactionRunner;

    private final SchemaManagerFactory schemaManagerFactory;

    DefaultPolyJDBC(Dialect dialect, ColumnTypeMapper typeMapper, TransactionManager transactionManager) {
        this.dialect = dialect;
        this.queryFactory = new QueryFactory(dialect, typeMapper);
        this.queryRunnerFactory = new QueryRunnerFactory(transactionManager, KeyGeneratorFactory.create(dialect));
        this.simpleQueryRunner = new SimpleQueryRunner(queryRunnerFactory);
        this.transactionRunner = new TransactionRunner(queryRunnerFactory);
        this.schemaManagerFactory = new SchemaManagerFactory(transactionManager);
    }

    public Dialect dialect() {
        return dialect;
    }

    public QueryFactory query() {
        return queryFactory;
    }

    public QueryRunner queryRunner() {
        return queryRunnerFactory.create();
    }

    public SimpleQueryRunner simpleQueryRunner() {
        return simpleQueryRunner;
    }

    public TransactionRunner transactionRunner() {
        return transactionRunner;
    }

    public SchemaManager schemaManager() {
        return schemaManagerFactory.createManager();
    }

    public SchemaInspector schemaInspector() {
        return schemaManagerFactory.createInspector();
    }

    public void rollback(QueryRunner... toRollback) {
        TheCloser.rollback(toRollback);
    }

    public void close(Closeable... toClose) {
        TheCloser.close(toClose);
    }
}
