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

import org.polyjdbc.core.exception.SequenceLimitReachedException;
import org.polyjdbc.core.transaction.Transaction;

import java.sql.SQLException;

/**
 * @author Adam Dubiel
 */
final class Sequence {

    private final String sequenceName;

    private long currentValue;

    private long currentLimit = -1;

    private final long allocationSize;

    Sequence(String sequenceName, long allocationSize) {
        this.allocationSize = allocationSize;
        this.sequenceName = sequenceName;
    }

    synchronized long nextValue(SequenceNextValQuery sequenceNextValQuery, Transaction transaction) throws SQLException{
        if (recalculationNeeded()) {
            long currentSequenceValue = sequenceNextValQuery.queryForNextVal(sequenceName, transaction);
            recalculate(currentSequenceValue);
        }
        return nextLocalValue();
    }

    long nextLocalValue() {
        if(recalculationNeeded()) {
            throw new SequenceLimitReachedException("Sequence " + sequenceName + " has reached its limit of " + currentLimit + ". "
                    + "Before fetching value, check if recalculation is needed using recalculationNeeded() method.");
        }
        currentValue++;
        return currentValue - 1;
    }

    void recalculate(long currentSequenceValue) {
        currentValue = allocationSize * currentSequenceValue ;
        currentLimit = allocationSize * (currentSequenceValue + 1) - 1;
    }

    boolean recalculationNeeded() {
        return currentValue > currentLimit;
    }
}
