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

import java.util.Arrays;
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
        InsertQuery insertQuery = query().insert().into("test").sequence("id", "seq_test")
                .value("name", "test").value("some_count", 42).value("countable", true)
                .value("separator_char", '|');
        QueryRunner queryRunner = queryRunner();

        // when
        long insertedId = queryRunner.insert(insertQuery);
        queryRunner.commitAndClose();

        // then
        assertThat(insertedId).isGreaterThanOrEqualTo(1);
        assertThat(polyJDBC()).contains("test").close();
    }

    @Test
    public void shouldRollbackInsertsMadeInTransaction() {
        // given
        InsertQuery insertQuery = query().insert().into("test").sequence("id", "seq_test")
                .value("name", "test").value("some_count", 42).value("countable", true)
                .value("separator_char", '|');
        QueryRunner queryRunner = queryRunner();

        // when
        queryRunner.insert(insertQuery);
        queryRunner.rollbackAndClose();

        // then
        assertThat(polyJDBC()).hasNoItems().close();
    }

    @Test
    public void shouldInsertWithoutUsingSequenceAndReturn0IfNoneSequenceDefinedInInsertQuery() {
        // given
        InsertQuery insertQuery = query().insert().into("test").value("id", 123)
                .value("name", "test").value("some_count", 42).value("countable", true)
                .value("separator_char", '|');
        QueryRunner queryRunner = queryRunner();

        // when
        long generatedId = queryRunner.insert(insertQuery);
        queryRunner.commitAndClose();

        // then
        assertThat(generatedId).isEqualTo(0);
    }

    @Test
    public void shouldListAllItemsInTable() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        SelectQuery selectQuery = query().selectAll().from("test");
        QueryRunner runner = queryRunner();

        // when
        List<Object> items = runner.queryList(selectQuery, new EmptyMapper());
        runner.commitAndClose();

        // then
        assertThat(items).hasSize(10);
    }

    @Test
    public void shouldReturnItemsMatchingContentsOfINClause() {
        // given
        database(queryRunner()).withItem("test1").withItem("test2").withItem("tes3").buildAndCloseTransaction();
        SelectQuery selectQuery = query().selectAll().from("test").where("name in (:name)")
                .withArgument("name", Arrays.asList("test1", "test2"));
        QueryRunner runner = queryRunner();

        // when
        List<Object> items = runner.queryList(selectQuery, new EmptyMapper());
        runner.commitAndClose();

        // then
        assertThat(items).hasSize(2);
    }

    @Test
    public void shouldReturnItemsMatchingContentsOfINClauseEvenIfNullElementPassed() {
        // given
        database(queryRunner()).withItem("test1").withItem("test2").withItem("tes3").buildAndCloseTransaction();
        SelectQuery selectQuery = query().selectAll().from("test").where("name in (:name)")
                .withArgument("name", new String[]{"test1", null});
        QueryRunner runner = queryRunner();

        // when
        List<Object> items = runner.queryList(selectQuery, new EmptyMapper());
        runner.commitAndClose();

        // then
        assertThat(items).hasSize(1);
    }

    @Test
    public void shouldReturnListOfItemsInSpecifiedOrder() {
        // given
        database(queryRunner()).withItem("last", "A", 10).withItem("second", "B", 45).withItem("first", "B", 43)
                .buildAndCloseTransaction();
        SelectQuery selectQuery = query().selectAll().from("test").orderBy("pseudo", Order.DESC).orderBy("some_count", Order.ASC);
        QueryRunner runner = queryRunner();

        // when
        List<TestItem> items = runner.queryList(selectQuery, new TestItemMapper());
        runner.commitAndClose();

        // then
        assertThat(items).containsExactly(new TestItem("B", 43), new TestItem("B", 45), new TestItem("A", 10));
    }

    @Test
    public void shouldReturnLimitedListOfItems() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        SelectQuery selectQuery = query().selectAll().from("test").limit(5);
        QueryRunner runner = queryRunner();

        // when
        List<TestItem> items = runner.queryList(selectQuery, new TestItemMapper());
        runner.commitAndClose();

        // then
        assertThat(items).hasSize(5);
    }

    @Test
    public void shouldReturnLimitedAndOffsettedListOfItems() {
        // given
        database(queryRunner()).withItem("A", "A", 10).withItem("B", "B", 45).withItem("C", "C", 43)
                .buildAndCloseTransaction();
        SelectQuery selectQuery = query().selectAll().from("test").orderBy("name", Order.ASC).limit(2, 1);
        QueryRunner runner = queryRunner();

        // when
        List<TestItem> items = runner.queryList(selectQuery, new TestItemMapper());
        runner.commitAndClose();

        // then
        assertThat(items).containsExactly(new TestItem("B", 45), new TestItem("C", 43));
    }

    @Test
    public void shouldFindUniqueItem() {
        // given
        database(queryRunner()).withItems(10).withItem("unique").buildAndCloseTransaction();
        SelectQuery selectQuery = query().selectAll().from("test").where("name = :name").withArgument("name", "unique");
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
        SelectQuery selectQuery = query().selectAll().from("test").where("some_count = :count").withArgument("count", 10);

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
        SelectQuery selectQuery = query().selectAll().from("test").where("name = :name").withArgument("name", "unknown");

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
        SelectQuery selectQuery = query().selectAll().from("test").where("name = :name").withArgument("name", "unknown");
        QueryRunner runner = queryRunner();

        // when
        Object item = runner.queryUnique(selectQuery, new EmptyMapper(), false);
        runner.commitAndClose();

        // then
        assertThat(item).isNull();
    }

    @Test
    public void shouldUpdateItemsInDatabase() {
        // given
        database(queryRunner()).withItem("test", "wrongPseudo", 10).buildAndCloseTransaction();
        QueryRunner runner = queryRunner();

        // when
        runner.update(query().update("test").set("pseudo", "goodPseudo").where("name = :name").withArgument("name", "test"));
        runner.commitAndClose();

        // then
        assertThat(polyJDBC()).contains(query().selectAll().from("test").where("pseudo = :pseudo").withArgument("pseudo", "goodPseudo")).close();
    }

    @Test
    public void shouldRunUpdateEvenIfQueryContainsFieldWithSameNameInSetAndWhereClause() {
        // given
        database(queryRunner()).withItem("test", "wrongPseudo", 10).buildAndCloseTransaction();
        QueryRunner runner = queryRunner();

        // when
        runner.update(query().update("test").set("pseudo", "goodPseudo").where("pseudo = :pseudo").withArgument("pseudo", "wrongPseudo"));
        runner.commitAndClose();

        // then
        assertThat(polyJDBC()).contains(query().selectAll().from("test").where("pseudo = :pseudo").withArgument("pseudo", "goodPseudo")).close();
    }

    @Test
    public void shouldReturnNumberOfEntriesChangedWhenUpdating() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        QueryRunner runner = queryRunner();

        // when
        int changedCount = runner.update(query().update("test").set("pseudo", "the same"));
        runner.commitAndClose();

        // then
        assertThat(changedCount).isEqualTo(10);
    }

    @Test
    public void shouldRollbackUpdatesMadeInTransaction() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        QueryRunner runner = queryRunner();
        runner.update(query().update("test").set("pseudo", "should be rollbacked"));

        // when
        runner.rollbackAndClose();

        // then
        assertThat(polyJDBC()).doesNotContain(query().selectAll().from("test").where("pseudo = :pseudo").withArgument("pseudo", "should be rollbacked")).close();
    }

    @Test
    public void shouldDeleteItemsFromDatabase() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        QueryRunner runner = queryRunner();

        // when
        runner.delete(query().delete().from("test"));
        runner.commitAndClose();

        // then
        assertThat(polyJDBC()).hasNoItems().close();
    }

    @Test
    public void shouldReturnNumberOfDeletedEntriesWhenDeleting() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        QueryRunner runner = queryRunner();

        // when
        int deletedCount = runner.delete(query().delete().from("test"));
        runner.commitAndClose();

        // then
        assertThat(deletedCount).isEqualTo(10);
    }

    @Test
    public void shouldRollbackDeletesMadeInTransaction() {
        // given
        database(queryRunner()).withItems(10).buildAndCloseTransaction();
        QueryRunner runner = queryRunner();
        runner.delete(query().delete().from("test"));

        // when
        runner.rollbackAndClose();

        // then
        assertThat(polyJDBC()).hasItems(10).close();
    }
}
