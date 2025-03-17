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

package org.polyjdbc.core.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.polyjdbc.core.query.mapper.ObjectMapper;

/**
 *
 * @author Adam Dubiel
 */
public class TypeTestItemMapper implements ObjectMapper<TypeTestItem> {

    @Override
    public TypeTestItem createObject(ResultSet resultSet) throws SQLException {
        TypeTestItem item = new TypeTestItem();

        item.string = resultSet.getString("string_attr");
        item.longAttr = resultSet.getLong("long_attr");
        item.integerAttr = resultSet.getInt("integer_attr");
        item.floatAttr = resultSet.getFloat("float_attr");
        item.number = resultSet.getBigDecimal("number_attr");
        item.booleanAttr = resultSet.getBoolean("boolean_attr");
        item.character = resultSet.getString("character_attr").charAt(0);
        item.text = resultSet.getString("text_attr");
        item.date = resultSet.getDate("date_attr");
        item.timestamp = resultSet.getTimestamp("timestamp_attr");
        item.json = resultSet.getString("json_attr");

        return item;
    }

}
