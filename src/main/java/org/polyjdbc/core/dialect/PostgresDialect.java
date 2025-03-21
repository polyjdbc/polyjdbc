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
package org.polyjdbc.core.dialect;

import org.polyjdbc.core.type.Json;
import org.polyjdbc.core.type.PostgresJson;

/**
 *
 * @author Adam Dubiel
 */
public class PostgresDialect extends AbstractDialect {

    private final PostgresDialectTypes types = new PostgresDialectTypes();

    @Override
    public String getCode() {
        return "POSTGRES";
    }

    @Override
    public String nextFromSequence(String sequenceName) {
        return "SELECT nextval('" + sequenceName + "')";
    }

    @Override
    public DialectTypes types() {
        return types;
    }

    @Override
    public boolean supportsNativeJsonColumnType() {
        return true;
    }

    @Override
    public Json json(String json) {
        return new PostgresJson(json);
    }
}
