package org.polyjdbc.core.key

import org.polyjdbc.core.PolyJDBC
import org.polyjdbc.core.PolyJDBCBuilder
import org.polyjdbc.core.dialect.DialectRegistry
import org.polyjdbc.core.infrastructure.DataSourceFactory
import org.polyjdbc.core.query.mapper.ObjectMapper
import org.polyjdbc.core.schema.model.Schema

import java.sql.ResultSet
import java.sql.SQLException

class PolyFactory {

    static PolyJDBC newPolyForPostgres() {
        def dialect = DialectRegistry.POSTGRES.dialect
        def dataSource = DataSourceFactory.create(dialect, 'jdbc:postgresql://localhost:5432/polly', 'javers', 'javers')

        PolyJDBCBuilder.polyJDBC(dialect, null).connectingToDataSource(dataSource).build()
    }

    static PolyJDBC newPolyForMySql() {
        def dialect = DialectRegistry.MYSQL.dialect
        def dataSource = DataSourceFactory.create(dialect, 'jdbc:mysql://localhost/polly', 'javers', '')

        PolyJDBCBuilder.polyJDBC(dialect, null).connectingToDataSource(dataSource).build()
    }

    static void createSchema(PolyJDBC polyJDBC) {
        def schema = new Schema(polyJDBC.dialect(), null);

        schema.addRelation("test")
                .withAttribute().longAttr("id").withAdditionalModifiers("AUTO_INCREMENT").and()
                .withAttribute().integer("some_count").and()
                .constrainedBy().primaryKey("pk_test").using("id").and()
                .build()
        schema.addSequence("seq_test").build()

        def manager = polyJDBC.schemaManager()
        manager.create(schema)
        polyJDBC.close(manager)
    }

    static int insertAndSelectOneRecord(long value, PolyJDBC polly) {
        def insertQuery = polly.query().insert().into("test").sequence("id", "seq_test")
                .value("some_count", value)
        def queryRunner = polly.queryRunner()

        long insertedId = queryRunner.insert(insertQuery)

        def selectQuery = polly.query().select("some_count").from("test").where("id = $insertedId");
        def selected = queryRunner.queryUnique(selectQuery, new ObjectMapper() {
            Object createObject(ResultSet resultSet) throws SQLException {
                resultSet.getLong("some_count")
            }
        })
        println "inserted $value at $insertedId, selected: $selected, thread: " + Thread.currentThread().name
        queryRunner.commitAndClose()

        selected
    }
}
