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

import java.math.BigDecimal;
import java.util.Date;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.polyjdbc.core.integration.DatabaseTest;
import org.polyjdbc.core.test.TypeTestItem;
import org.polyjdbc.core.test.TypeTestItemMapper;
import org.polyjdbc.core.type.Timestamp;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.polyjdbc.core.query.QueryFactory.*;

/**
 *
 * @author Adam Dubiel
 */
@Test(groups = "integration")
public class QueryColumnTypesTest extends DatabaseTest {

    @Test
    public void shouldPersistAndReadStringColumn() {
        // given
        String persistedString = "I'm a string";
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("string_attr", persistedString);
        SelectQuery select = selectAll().from("type_test").where("code = :code").withArgument("code", "test");

        QueryRunner runner = queryRunner();

        // when
        runner.insert(insert);
        runner.commit();
        TypeTestItem item = runner.queryUnique(select, new TypeTestItemMapper());
        runner.close();

        // then
        assertThat(item.getString()).isEqualTo(persistedString);
    }

    @Test
    public void shouldPersistAndReadLongColumn() {
        // given
        long persistedLong = 124L;
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("long_attr", persistedLong);
        SelectQuery select = selectAll().from("type_test").where("code = :code").withArgument("code", "test");

        QueryRunner runner = queryRunner();

        // when
        runner.insert(insert);
        runner.commit();
        TypeTestItem item = runner.queryUnique(select, new TypeTestItemMapper());
        runner.close();

        // then
        assertThat(item.getLongAttr()).isEqualTo(persistedLong);
    }

    @Test
    public void shouldPersistAndReadIntegerColumn() {
        // given
        int persistedInt = 1241;
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("integer_attr", persistedInt);
        SelectQuery select = selectAll().from("type_test").where("code = :code").withArgument("code", "test");

        QueryRunner runner = queryRunner();

        // when
        runner.insert(insert);
        runner.commit();
        TypeTestItem item = runner.queryUnique(select, new TypeTestItemMapper());
        runner.close();

        // then
        assertThat(item.getIntegerAttr()).isEqualTo(persistedInt);
    }
    
    @Test
    public void shouldPersistAndReadFloatColumn() {
        // given
        float persistedFloat = 124.12F;
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("float_attr", persistedFloat);
        SelectQuery select = selectAll().from("type_test").where("code = :code").withArgument("code", "test");

        QueryRunner runner = queryRunner();

        // when
        runner.insert(insert);
        runner.commit();
        TypeTestItem item = runner.queryUnique(select, new TypeTestItemMapper());
        runner.close();

        // then
        assertThat(item.getFloatAttr()).isEqualTo(persistedFloat);
    }
    
    @Test
    public void shouldPersistAndReadNumberColumn() {
        // given
        BigDecimal persistedNumber = BigDecimal.valueOf(124.12);
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("number_attr", persistedNumber);
        SelectQuery select = selectAll().from("type_test").where("code = :code").withArgument("code", "test");

        QueryRunner runner = queryRunner();

        // when
        runner.insert(insert);
        runner.commit();
        TypeTestItem item = runner.queryUnique(select, new TypeTestItemMapper());
        runner.close();

        // then
        assertThat(item.getNumber()).isEqualTo(persistedNumber);
    }

    @Test
    public void shouldPersistAndReadBooleanColumn() {
        // given
        boolean persistedBoolean = true;
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("boolean_attr", persistedBoolean);
        SelectQuery select = selectAll().from("type_test").where("code = :code").withArgument("code", "test");

        QueryRunner runner = queryRunner();

        // when
        runner.insert(insert);
        runner.commit();
        TypeTestItem item = runner.queryUnique(select, new TypeTestItemMapper());
        runner.close();

        // then
        assertThat(item.getBooleanAttr()).isEqualTo(persistedBoolean);
    }

    @Test
    public void shouldPersistAndReadCharacterColumn() {
        // given
        char persistedChar = 'A';
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("character_attr", persistedChar);
        SelectQuery select = selectAll().from("type_test").where("code = :code").withArgument("code", "test");

        QueryRunner runner = queryRunner();

        // when
        runner.insert(insert);
        runner.commit();
        TypeTestItem item = runner.queryUnique(select, new TypeTestItemMapper());
        runner.close();

        // then
        assertThat(item.getCharacter()).isEqualTo(persistedChar);
    }

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
        assertThat(item.getTimestamp().getTime()).isEqualTo(persistedDate.getTime());
    }

    @Test
    public void shouldPersistAndReadTextColumn() {
        // given
        String persistedText = "Hello, this is a very very long text";
        InsertQuery insert = insert().into("type_test").value("code", "test")
                .value("text_attr", persistedText);
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
