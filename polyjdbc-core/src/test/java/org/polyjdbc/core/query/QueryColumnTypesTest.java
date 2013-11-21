/*
 * Copyright 2013 Adam Dubiel.
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

import java.util.Date;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.integration.DatabaseTest;
import org.polyjdbc.core.test.TypeTestItem;
import org.polyjdbc.core.test.TypeTestItemMapper;
import org.polyjdbc.core.type.Text;
import org.polyjdbc.core.type.Timestamp;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.polyjdbc.core.query.QueryFactory.*;

/**
 *
 * @author Adam Dubiel
 */
@Test(groups = "integration")
public class QueryColumnTypesTest extends DatabaseTest {

    @Test
    public void shouldPersistAndReadDateColumn() {
        // given
        Date persistedDate = new LocalDate(2013, 5, 2).toDate();
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("date_attr", persistedDate);
        SelectQuery select = selectAll().from("type_test").where("code = :code").withArgument("code", "test");

        QueryRunner runner = queryRunner();

        // when
        runner.insert(insert);
        runner.commit();
        TypeTestItem item = runner.queryUnique(select, new TypeTestItemMapper());
        runner.close();

        // then
        assertThat(item.getDate()).isEqualTo(persistedDate);
    }

    @Test
    public void shouldPersistAndReadTimestampColumn() {
        // given
        Date persistedDate = new LocalDateTime(2013, 5, 2, 15, 21, 59).toDate();
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("timestamp_attr", Timestamp.from(persistedDate));
        SelectQuery select = selectAll().from("type_test").where("code = :code").withArgument("code", "test");

        QueryRunner runner = queryRunner();

        // when
        runner.insert(insert);
        runner.commit();
        TypeTestItem item = runner.queryUnique(select, new TypeTestItemMapper());
        runner.close();

        // then
        assertThat(item.getTimestamp()).isEqualTo(persistedDate);
    }

    @Test
    public void shouldPersistAndReadTextColumn() {
        // given
        String persistedText = "Hello, this is a very very long text";
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("text_attr", Text.from(persistedText));
        SelectQuery select = selectAll().from("type_test").where("code = :code").withArgument("code", "test");

        QueryRunner runner = queryRunner();

        // when
        runner.insert(insert);
        runner.commit();
        TypeTestItem item = runner.queryUnique(select, new TypeTestItemMapper());
        runner.close();

        // then
        assertThat(item.getText()).isEqualTo(persistedText);
    }
}
