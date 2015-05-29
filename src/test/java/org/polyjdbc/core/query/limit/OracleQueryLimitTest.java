package org.polyjdbc.core.query.limit;

import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.PolyJDBCBuilder;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;
import org.polyjdbc.core.infrastructure.DataSourceFactory;
import org.polyjdbc.core.integration.TestSchemaManager;
import org.polyjdbc.core.query.Order;
import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.test.DatabaseBuilder;
import org.polyjdbc.core.test.TestItem;
import org.polyjdbc.core.test.TestItemMapper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class OracleQueryLimitTest {

    private DatabaseBuilder database;
    private PolyJDBC polyJDBC;

    @BeforeClass
    public void setup() {
        Dialect dialect = DialectRegistry.ORACLE.getDialect();

        DataSource dataSource = DataSourceFactory.create(dialect, "jdbc:oracle:thin:@192.168.59.103:49161:xe", "system", "oracle");
        polyJDBC = PolyJDBCBuilder.polyJDBC(dialect).connectingToDataSource(dataSource).build();
        database = DatabaseBuilder.database(polyJDBC);

        TestSchemaManager schemaManager = new TestSchemaManager(polyJDBC);
        schemaManager.createSchema();
    }

    @Test
    public void shouldReturnLimitedListOfItems() {
        // given
        database.withItems(10).buildAndCloseTransaction();
        SelectQuery selectQuery = polyJDBC.query().selectAll().from("test").limit(5);
        QueryRunner runner = polyJDBC.queryRunner();

        // when
        List<TestItem> items = runner.queryList(selectQuery, new TestItemMapper());
        runner.commitAndClose();

        // then
        assertThat(items).hasSize(5);
    }

    @Test
    public void shouldReturnLimitedAndOffsettedListOfItems() {
        // given
        database.withItem("A", "A", 10).withItem("B", "B", 45).withItem("C", "C", 43)
                .buildAndCloseTransaction();
        SelectQuery selectQuery = polyJDBC.query().selectAll().from("test").orderBy("name", Order.ASC).limit(2, 1);
        QueryRunner runner = polyJDBC.queryRunner();

        // when
        List<TestItem> items = runner.queryList(selectQuery, new TestItemMapper());
        runner.commitAndClose();

        // then
        assertThat(items).containsExactly(new TestItem("B", 45), new TestItem("C", 43));
    }

}
