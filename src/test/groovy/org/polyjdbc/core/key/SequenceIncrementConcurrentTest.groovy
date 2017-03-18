package org.polyjdbc.core.key

import org.polyjdbc.core.PolyJDBC
import org.polyjdbc.core.PolyJDBCBuilder
import org.polyjdbc.core.dialect.DialectRegistry
import org.polyjdbc.core.infrastructure.DataSourceFactory
import org.polyjdbc.core.infrastructure.TheCleaner
import org.polyjdbc.core.query.mapper.ObjectMapper
import org.polyjdbc.core.schema.model.Schema
import spock.lang.Shared
import spock.lang.Specification

import java.sql.ResultSet
import java.sql.SQLException
import java.util.concurrent.atomic.AtomicInteger

import static groovyx.gpars.GParsPool.withPool

/**
 * @author bartosz.walacik
 */
class SequenceIncrementConcurrentTest extends Specification{
    @Shared TheCleaner cleaner
    @Shared PolyJDBC polyJDBC

    def setupSpec() {
        this.polyJDBC = newPolyJDBC()
        this.cleaner = new TheCleaner(polyJDBC)

        if (!polyJDBC.schemaInspector().relationExists('test')) {
            new TestSchemaManager().createSchema()
        }
    }


    def cleanupSpec() {
        //cleaner.cleanDB('test')
    }

    PolyJDBC newPolyJDBC() {
        def dialect = DialectRegistry.POSTGRES.dialect
        def dataSource = DataSourceFactory.create(dialect, 'jdbc:postgresql://localhost:5432/polly', 'javers', 'javers')

        PolyJDBCBuilder.polyJDBC(dialect, null).connectingToDataSource(dataSource).build()
    }

    def "should manage concurrent writes from a single pollyJDBC instance"() {
      given:

      def cnt = new AtomicInteger()
      def threads = 101

      when:
      withPool threads, {
          (1..threads).collectParallel {
              insertAndSelectOneRecord(it, cnt, this.polyJDBC)
              insertAndSelectOneRecord(threads + it, cnt, this.polyJDBC)
          }
      }

      then:
      cnt.get() == threads * 2
    }

    def "should manage concurrent writes from many pollyJDBC instances"() {
        given:

        insertAndSelectOneRecord(0, new AtomicInteger(), this.polyJDBC) //init db

        def cnt = new AtomicInteger()
        def threads = 20

        when:
        withPool threads, {
            (1..threads).collectParallel {
                def t = it
                def polly = newPolyJDBC()
                20.times {
                    insertAndSelectOneRecord(t*100+it, cnt, polly)
                }
            }
        }

        then:
        cnt.get() == threads * 20
    }

    int insertAndSelectOneRecord(long value, AtomicInteger cnt, PolyJDBC polly) {
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

        if ( value == selected ) {
            cnt.incrementAndGet()
        }
    }

    class TestSchemaManager {
        void createSchema() {
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
    }
}
