package org.polyjdbc.core.key

import org.polyjdbc.core.PolyJDBC
import spock.lang.Shared
import spock.lang.Specification

import static org.polyjdbc.core.key.PolyFactory.insertAndSelectOneRecord

class MySqlAutoincrementInsertTest extends Specification {
    @Shared PolyJDBC polyJDBC

    def setupSpec() {
        this.polyJDBC = createPoly()

        if (!polyJDBC.schemaInspector().relationExists('test')) {
            PolyFactory.createSchema(polyJDBC)
        }
    }

    private PolyJDBC createPoly() {
        PolyFactory.newPolyForMySql()
    }

    def "should not use sequence for MySql "(){
        given:
        def insertQuery = createPoly().query().insert().into("test").sequence("id", "seq_test")

        expect:
        !insertQuery.sequenceUsed
    }

    def "should insert records with autoincremented id"(){
        given:
        def recordsToInsert = 100
        def inserted = 0

        when:
        recordsToInsert.times {
            def selected = insertAndSelectOneRecord(it, this.polyJDBC)
            if (selected == it) inserted ++
        }

        then:
        inserted == recordsToInsert
    }
}
