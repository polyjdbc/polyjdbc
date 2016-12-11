package org.polyjdbc.core.key

import org.polyjdbc.core.transaction.Transaction
import spock.lang.Specification

import java.util.concurrent.ConcurrentHashMap

import static groovyx.gpars.GParsPool.withPool

class SequenceAllocationTest extends Specification {
    int INITIAL_SHIFT = 99

    def "should generate keys allocating 100 keys for each value fetched form db sequence"() {
        given:
        def sequenceNextValQueryStub = Stub(SequenceNextValQuery) {
            queryForNextVal(_, _) >>> [1, 2]
        }
        def transactionStub = Stub(Transaction)

        def sequenceAllocation = new SequenceAllocation(sequenceNextValQueryStub)

        expect:
        (1..200).each {
            def val = sequenceAllocation.generateKey("seq1", transactionStub)
            assert val == it + INITIAL_SHIFT
        }
    }

    def "should generate keys thread safely "(){
        given:
        def sequenceNextValQueryStub = Stub(SequenceNextValQuery) {
            queryForNextVal(_,_) >>> (1..100).collect{it}
        }
        def transactionStub = Stub(Transaction)

        def sequenceAllocation = new SequenceAllocation(sequenceNextValQueryStub)
        def values = new ConcurrentHashMap()
        def threads = 25
        def rounds = 25

        when:
        withPool threads, {
            (1..threads).collectParallel {
                (1..rounds).each {
                    def val = sequenceAllocation.generateKey("seq1", transactionStub)
                    values.put(val,'')
                }
            }
        }

        then:
        def max = values.keySet().max()
        println 'max: ' + max

        max == rounds * threads + INITIAL_SHIFT
    }
}
