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
import org.polyjdbc.core.exception.NonUniqueException;
import org.polyjdbc.core.integration.DatabaseTest;
import org.polyjdbc.core.query.mapper.EmptyMapper;
import org.polyjdbc.core.test.TestItem;
import org.polyjdbc.core.test.TestItemMapper;
import org.testng.annotations.Test;
import static org.polyjdbc.core.test.DatabaseBuilder.database;
import static org.polyjdbc.core.test.assertions.PolyJdbcAssertions.*;

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
                .value("name", "test").value("some_count", 42).value("countable", true)
                .value("separator_char", '|');
        QueryRunner queryRunner = queryRunner();

        // when
        long insertedId = queryRunner.insert(insertQuery);
        queryRunner.close();

        // then
        assertThat(insertedId).isGreaterThanOrEqualTo(1);
        assertThat(queryRunner()).contains("test").close();
    }

    @Test
    public void shouldRollbackInsertsMadeInTransaction() {
        // given
        InsertQuery insertQuery = QueryFactory.insert().into("test").sequence("id", "seq_test")
                .value("name", "test").value("some_count", 42).value("countable", true)
                .value("separator_char", '|');
        QueryRunner queryRunner = queryRunner();

        // when
        queryRunner.insert(insertQuery);
        queryRunner.rollbackAndClose();

        // then
        assertThat(queryRunner()).hasNoItems().close();
    }

    @Test
    public void shouldListAllItemsInTable() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.selectAll().from("test");
        QueryRunner runner = queryRunner();

        // when
        List<Object> items = runner.queryList(selectQuery, new EmptyMapper());
        runner.close();

        // then
        assertThat(items).hasSize(10);
    }

    @Test
    public void shouldReturnListOfItemsInSpecifiedOrder() {
        // given
        database(queryRunner()).withItem("last", "A", 10).withItem("second", "B", 45).withItem("first", "B", 43)
                .buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.selectAll().from("test").orderBy("pseudo", Order.DESC).orderBy("some_count", Order.ASC);
        QueryRunner runner = queryRunner();

        // when
        List<TestItem> items = runner.queryList(selectQuery, new TestItemMapper());
        runner.close();

        // then
        assertThat(items).containsExactly(new TestItem("B", 43), new TestItem("B", 45), new TestItem("A", 10));
    }

        @Test
    public void shouldReturnLimitedListOfItems() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.selectAll().from("test").limit(5);
        QueryRunner runner = queryRunner();

        // when
        List<TestItem> items = runner.queryList(selectQuery, new TestItemMapper());
        runner.close();

        // then
        assertThat(items).hasSize(5);
    }

    @Test
    public void shouldReturnLimitedAndOffsettedListOfItems() {
        // given
        database(queryRunner()).withItem("A", "A", 10).withItem("B", "B", 45).withItem("C", "C", 43)
                .buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.selectAll().from("test").orderBy("name", Order.ASC).limit(2, 1);
        QueryRunner runner = queryRunner();

        // when
        List<TestItem> items = runner.queryList(selectQuery, new TestItemMapper());
        runner.close();

        // then
        assertThat(items).containsExactly(new TestItem("B", 45), new TestItem("C", 43));
    }

    @Test
    public void shouldFindUniqueItem() {
        // given
        database(queryRunner()).withItems(10).withItem("unique").buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.selectAll().from("test").where("name = :name").withArgument("name", "unique");
        QueryRunner runner = queryRunner();

        // when
        Object item = runner.queryUnique(selectQuery, new EmptyMapper());
        runner.close();

        // then
        assertThat(item).isNotNull();
    }

    @Test
    public void shouldThrowExceptionWithDistinctCodeWhenMoreThanOneItemFoundWhileLookingForUnique() {
        // given
        database(queryRunner()).withItems(10).withItem("unique", 10).withItem("unique2", 10).buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.selectAll().from("test").where("some_count = :count").withArgument("count", 10);

        // when
        try {
            queryRunner().queryUnique(selectQuery, new EmptyMapper());
            fail("expected NonUniqueException");
        } catch (NonUniqueException exception) {
            // then
            assertThat(exception).hasCode("NON_UNIQUE_ITEM");
        } finally {
            queryRunner().close();
        }
    }

    @Test
    public void shouldThrowExceptionWithDistinctCodeWhenNoItemFoundWhileLookingForUnique() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.selectAll().from("test").where("name = :name").withArgument("name", "unknown");

        // when
        try {
            queryRunner().queryUnique(selectQuery, new EmptyMapper());
            fail("expected NonUniqueException");
        } catch (NonUniqueException exception) {
            // then
            assertThat(exception).hasCode("NO_ITEM_FOUND");
        } finally {
            queryRunner().close();
        }
    }

    @Test
    public void shouldReturnNullWhenNotFindingUniqueWithExceptionsSuppressed() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        SelectQuery selectQuery = QueryFactory.selectAll().from("test").where("name = :name").withArgument("name", "unknown");
        QueryRunner runner = queryRunner();

        // when
        Object item = runner.queryUnique(selectQuery, new EmptyMapper(), false);
        runner.close();

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
        runner.close();

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
        runner.rollbackAndClose();

        // then
        assertThat(queryRunner()).hasItems(10).close();
    }
}
