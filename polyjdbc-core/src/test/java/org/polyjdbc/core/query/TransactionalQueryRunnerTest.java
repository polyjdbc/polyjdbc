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
package org.polyjdbc.core.query;

import java.util.List;
import org.polyjdbc.core.exception.PolyJdbcException;
import org.polyjdbc.core.integration.DatabaseTest;
import org.polyjdbc.core.query.mapper.EmptyMapper;
import org.testng.annotations.Test;
import static com.googlecode.catchexception.CatchException.*;
import static org.polyjdbc.core.test.assertions.PolyJdbcAssertions.*;
import static org.polyjdbc.core.test.DatabaseBuilder.database;

/**
 *
 * @author Adam Dubiel
 */
@Test(groups = "integration")
public class TransactionalQueryRunnerTest extends DatabaseTest {

    @Test
    public void shouldInsertRecordAndReturnId() {
        // given
        InsertQuery insertQuery = QueryFactory.insert().into("test").sequence("id", "seq_test")
                .value("name", "test").value("count", 42).value("countable", true)
                .value("separator", '|');
        QueryRunner queryRunner = queryRunner();

        // when
        long insertedId = queryRunner.insert(insertQuery);
        queryRunner.commitAndClose();

        // then
        assertThat(insertedId).isGreaterThanOrEqualTo(100);
        assertThat(queryRunner()).contains("test").close();
    }

    @Test
    public void shouldRollbackInsertsMadeInTransaction() {
        // given
        InsertQuery insertQuery = QueryFactory.insert().into("test").sequence("id", "seq_test")
                .value("name", "test").value("count", 42).value("countable", true)
                .value("separator", '|');
        QueryRunner queryRunner = queryRunner();

        // when
        queryRunner.insert(insertQuery);
        queryRunner.rollback();

        // then
        assertThat(queryRunner()).hasNoItems().close();
    }

    @Test
    public void shouldRollbackChangesMadeInTransactionWhenClosingWithoutCommit() {
        // given
        InsertQuery insertQuery = QueryFactory.insert().into("test").sequence("id", "seq_test")
                .value("name", "test").value("count", 42).value("countable", true)
                .value("separator", '|');
        QueryRunner queryRunner = queryRunner();

        // when
        queryRunner.insert(insertQuery);
        queryRunner.close();

        // then
        assertThat(queryRunner()).hasNoItems().close();
    }

    @Test
    public void shouldListAllItemsInTable() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.select().query("select * from test");
        QueryRunner runner = queryRunner();

        // when
        List<Object> items = runner.queryList(selectQuery, new EmptyMapper());
        runner.commitAndClose();

        // then
        assertThat(items).hasSize(10);
    }

    @Test
    public void shouldFindUniqueItem() {
        // given
        database(queryRunner()).withItems(10).withItem("unique").buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.select().query("select * from test where name = :name").withArgument("name", "unique");
        QueryRunner runner = queryRunner();

        // when
        Object item = runner.queryUnique(selectQuery, new EmptyMapper());
        runner.commitAndClose();

        // then
        assertThat(item).isNotNull();
    }

    @Test
    public void shouldThrowExceptionWithDistinctCodeWhenMoreThanOneItemFoundWhileLookingForUnique() {
        // given
        database(queryRunner()).withItems(10).withItem("unique", 10).withItem("unique2", 10).buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.select().query("select * from test where count = :count").withArgument("count", 10);

        // when
        catchException(queryRunner()).queryUnique(selectQuery, new EmptyMapper());

        // then
        assertThat((PolyJdbcException) caughtException()).hasCode("NON_UNIQUE_ITEM");
    }

    @Test
    public void shouldThrowExceptionWithDistinctCodeWhenNoItemFoundWhileLookingForUnique() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.select().query("select * from test where name = :name").withArgument("name", "unknown");

        // when
        catchException(queryRunner()).queryUnique(selectQuery, new EmptyMapper());

        // then
        assertThat((PolyJdbcException) caughtException()).hasCode("NO_ITEM_FOUND");
    }

    @Test
    public void shouldReturnNullWhenNotFindingUniqueWithExceptionsSuppressed() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.select().query("select * from test where name = :name").withArgument("name", "unknown");
        QueryRunner runner = queryRunner();

        // when
        Object item = runner.queryUnique(selectQuery, new EmptyMapper(), false);
        runner.commitAndClose();

        // then
        assertThat(item).isNull();
    }

    @Test
    public void shouldDeleteItemsFromDatabase() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        QueryRunner runner = queryRunner();

        // when
        runner.delete(QueryFactory.delete().from("test"));
        runner.commitAndClose();

        // then
        assertThat(queryRunner()).hasNoItems().close();
    }

    @Test
    public void shouldRollbackDeletesMadeInTransaction() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        QueryRunner runner = queryRunner();
        runner.delete(QueryFactory.delete().from("test"));

        // when
        runner.rollback();

        // then
        assertThat(queryRunner()).hasItems(10).close();
    }
}
