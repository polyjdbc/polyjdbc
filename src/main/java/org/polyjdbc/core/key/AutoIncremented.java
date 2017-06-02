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
import org.polyjdbc.core.transaction.Transaction;

/**
 *
 * @author Adam Dubiel
 */
public class AutoIncremented implements KeyGenerator {
    @Override
    public long generateKey(String sequenceName, Transaction transaction) throws SQLException {
        throw new RuntimeException("Not implemented. Can't generate key on AutoIncremented");
    }

    @Override
    public long getKeyFromLastInsert(Transaction transaction) throws SQLException {
        try (PreparedStatement statement = transaction.prepareStatement("select last_insert_id()")) {
            ResultSet resultSet = statement.executeQuery();
            transaction.registerCursor(resultSet);

            resultSet.next();
            return resultSet.getLong(1);
        }
    }

    public void reset() {
    }
}
