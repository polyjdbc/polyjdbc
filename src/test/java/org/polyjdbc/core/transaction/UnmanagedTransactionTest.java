package org.polyjdbc.core.transaction;

import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.PolyJDBCBuilder;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;
import org.polyjdbc.core.infrastructure.DataSourceFactory;
import org.polyjdbc.core.integration.TestSchemaManager;
import org.polyjdbc.core.key.KeyGeneratorRegistry;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.query.TransactionalQueryRunner;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.polyjdbc.core.test.assertions.PolyJdbcAssertions.assertThat;

public class UnmanagedTransactionTest {

    private final Dialect dialect = DialectRegistry.H2.getDialect();

    private PolyJDBC polyJDBC;

    private DataSource dataSource;

    private Connection connection;

    private QueryRunner queryRunner;

    private Transaction transaction;

    @BeforeClass
    public void setUpDatabase() {
        dataSource = DataSourceFactory.create(dialect, "jdbc:h2:mem:unmanaged_test", "polly", "polly");
        polyJDBC = PolyJDBCBuilder.polyJDBC(dialect).connectingToDataSource(dataSource).build();
        new TestSchemaManager(polyJDBC).createSchema();
    }

    @BeforeMethod
    public void setUp() throws SQLException {
        connection = dataSource.getConnection();
        this.transaction = new Transaction(connection, new ExternalTransactionState());
        this.queryRunner = new TransactionalQueryRunner(transaction, KeyGeneratorRegistry.keyGenerator(dialect));
    }

    @Test
    public void shouldNotCommitTransactionWhenWorkingWithExternalConnections() throws SQLException {
        // given
        InsertQuery query = polyJDBC.query().insert().into("test").sequence("id", "seq_test").value("name", "hello");

        // when
        queryRunner.insert(query);
        queryRunner.commit();
        connection.close();

        // then
        assertThat(polyJDBC).doesNotContain(polyJDBC.query().selectAll().from("test"));
    }

    @Test
    public void shouldNotCloseTransactionWhenWorkingWithExternalConnections() throws SQLException {
        // given when
        queryRunner.close();

        // then
        assertThat(transaction.getConnection().isClosed()).isFalse();
        connection.close();
    }

    @Test
    public void shouldNotRollbackTransactionWhenWorkingWithExternalConnections() throws SQLException {
        // given
        InsertQuery query = polyJDBC.query().insert().into("test").sequence("id", "seq_test").value("name", "hello");

        // when
        queryRunner.insert(query);
        queryRunner.rollback();

        // then
        assertThat(queryRunner.queryExistence(polyJDBC.query().selectAll().from("test"))).isTrue();
        connection.close();
    }
}
