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
package org.polyjdbc.core.test;

import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.QueryFactory;
import org.polyjdbc.core.query.QueryRunner;

/**
 *
 * @author Adam Dubiel
 */
public class DatabaseBuilder {

    private QueryRunner queryRunner;

    private int insertedItems;

    private DatabaseBuilder(QueryRunner queryRunner) {
        this.queryRunner = queryRunner;
    }

    public static DatabaseBuilder database(QueryRunner queryRunner) {
        return new DatabaseBuilder(queryRunner);
    }

    public void build() {
        queryRunner.commitAndClose();
    }

    public DatabaseBuilder withItem(String name, int count) {
        InsertQuery insertQuery = QueryFactory.insert().into("test").sequence("id", "seq_test")
                .value("name", name).value("count", count).value("countable", true)
                .value("separator", '|');
        queryRunner.insert(insertQuery);
        return this;
    }

    public DatabaseBuilder withItem(String name) {
        return withItem(name, 42);
    }

    public DatabaseBuilder withItems(int count) {
        for (int i = 0; i < count; ++i) {
            withItem("testItem" + insertedItems);
            insertedItems++;
        }

        return this;
    }
}
