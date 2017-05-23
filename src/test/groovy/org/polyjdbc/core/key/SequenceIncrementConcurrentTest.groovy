package org.polyjdbc.core.key

import org.polyjdbc.core.PolyJDBC
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.atomic.AtomicInteger

import static groovyx.gpars.GParsPool.withPool
import static org.polyjdbc.core.key.PolyFactory.insertAndSelectOneRecord

/**
 * @author bartosz.walacik
 */
class SequenceIncrementConcurrentTest extends Specification{
    @Shared PolyJDBC polyJDBC

    def setupSpec() {
        this.polyJDBC = createPoly()

        if (!polyJDBC.schemaInspector().relationExists('test')) {
            PolyFactory.createSchema(polyJDBC)
        }
    }

    private PolyJDBC createPoly() {
        PolyFactory.newPolyForPostgres()
    }

    def "should manage concurrent writes from a single pollyJDBC instance"() {
      given:
      def cnt = new AtomicInteger()
      def threads = 101

      when:
      withPool threads, {
          (1..threads).collectParallel {
              def selected = insertAndSelectOneRecord(it, this.polyJDBC)
              if (selected == it) cnt.incrementAndGet()

              selected = insertAndSelectOneRecord(threads + it, this.polyJDBC)
              if (selected == threads + it) cnt.incrementAndGet()
          }
      }

      then:
      cnt.get() == threads * 2
    }

    def "should manage concurrent writes from many pollyJDBC instances"() {
        given:

        insertAndSelectOneRecord(0, this.polyJDBC) //init db

        def cnt = new AtomicInteger()
        def threads = 20

        when:
        withPool threads, {
            (1..threads).collectParallel {
                def t = it
                def polly = createPoly()
                20.times {
                    def value = t*100+it
                    def selected = insertAndSelectOneRecord(value,  polly)

                    if ( value == selected ) {
                        cnt.incrementAndGet()
                    }
                }
            }
        }

        then:
        cnt.get() == threads * 20
    }
}
