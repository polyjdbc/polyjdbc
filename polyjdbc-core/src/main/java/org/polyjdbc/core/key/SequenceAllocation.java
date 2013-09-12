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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.polyjdbc.core.transaction.Transaction;

/**
 *
 * @author Adam Dubiel
 */
public class SequenceAllocation implements KeyGenerator {

    private static final long SEQUENCE_ALLOCATION_SIZE = 100;

    private SequenceNextValGenerator sequenceNextValGenerator;

    private Map<String, Sequence> sequences = new HashMap<String, Sequence>();

    private long lastKey;

    public SequenceAllocation(SequenceNextValGenerator sequenceNextValGenerator) {
        this.sequenceNextValGenerator = sequenceNextValGenerator;
    }

    @Override
    public long generateKey(String sequenceName, Transaction transaction) throws SQLException {
        Sequence sequence = findSequence(sequenceName);
        if (sequence.recalculationNeeded()) {
            long currentSequenceValue = fetchSequenceValue(sequenceName, transaction);
            sequence.recalculate(currentSequenceValue);
        }
        lastKey = sequence.nextValue();
        return lastKey;
    }

    private Sequence findSequence(String sequenceName) {
        if (sequences.containsKey(sequenceName)) {
            return sequences.get(sequenceName);
        } else {
            Sequence sequence = new Sequence(sequenceName, SEQUENCE_ALLOCATION_SIZE);
            sequences.put(sequenceName, sequence);
            return sequence;
        }
    }

    private long fetchSequenceValue(String sequenceName, Transaction transaction) throws SQLException {
        PreparedStatement statement = transaction.getConnection().prepareStatement(sequenceNextValGenerator.nextval(sequenceName));
        transaction.registerPrepareStatement(statement);
        ResultSet resultSet = statement.executeQuery();
        transaction.registerCursor(resultSet);

        resultSet.next();
        return resultSet.getLong(1);
    }

    @Override
    public long getKeyFromLastInsert(Transaction transaction) {
        return lastKey;
    }
}
