/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.polyjdbc.core.key;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.transaction.Transaction;

import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Adam Dubiel
 */
public class SequenceAllocation implements KeyGenerator {

    private static final long SEQUENCE_ALLOCATION_SIZE = 100;

    private final Object lock = new Object();

    private final SequenceNextValQuery sequenceNextValQuery;

    private Map<String, Sequence> sequences = new ConcurrentHashMap<String, Sequence>();

    private ThreadLocal<Long> lastKey = new ThreadLocal<Long>();

    public SequenceAllocation(Dialect dialect) {
        this.sequenceNextValQuery = new SequenceNextValQuery(dialect);
    }

    SequenceAllocation(SequenceNextValQuery sequenceNextValQuery) {
        this.sequenceNextValQuery = sequenceNextValQuery;
    }

    @Override
    public long generateKey(String sequenceName, Transaction transaction) throws SQLException {
        long nextVal = findSequence(sequenceName).nextValue(sequenceNextValQuery, transaction);
        lastKey.set(nextVal);
        return nextVal;
    }

    private Sequence findSequence(String sequenceName) {
        if (!sequences.containsKey(sequenceName)) {
            synchronized (lock) {
                //double check, condition could change while obtaining the lock
                if (!sequences.containsKey(sequenceName)) {
                    Sequence sequence = new Sequence(sequenceName, SEQUENCE_ALLOCATION_SIZE);
                    sequences.put(sequenceName, sequence);
                }
            }
        }

        return sequences.get(sequenceName);
    }

    @Override
    public long getKeyFromLastInsert(Transaction transaction) {
        return lastKey.get();
    }

    public void reset() {
        synchronized (lock) {
            sequences.clear();
        }
    }
}
