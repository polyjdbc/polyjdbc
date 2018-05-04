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

package org.polyjdbc.core.schema.model;

import org.junit.Test;
import org.polyjdbc.core.dialect.H2Dialect;
import static org.assertj.core.api.Assertions.assertThat;
import static org.polyjdbc.core.schema.model.Relation.Builder.relation;

/**
 *
 * @author Adam Dubiel
 */
public class RelationTest {

    @Test
    public void shouldBecomeValidDDLWhenToStringCalled() {
        // given
        Relation relation = relation(new H2Dialect(), "test")
                .withAttribute().longAttr("id").and()
                .withAttribute().string("name").unique().notNull().withMaxLength(255).and()
                .constrainedBy().primaryKey("pk").using("id").and()
                .build();

        // when
        String ddl = relation.toString();

        // then
        String expected = "CREATE TABLE test (\n"
                + "id BIGINT,\n"
                + "name VARCHAR(255) UNIQUE NOT NULL,\n"
                + "CONSTRAINT pk PRIMARY KEY(id)\n"
                + ")";

        assertThat(ddl.trim()).isEqualTo(expected);
    }

}