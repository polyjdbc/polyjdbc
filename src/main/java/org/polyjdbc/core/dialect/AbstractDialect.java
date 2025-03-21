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

import org.polyjdbc.core.type.BasicJson;
import org.polyjdbc.core.type.Json;

/**
 *
 * @author Adam Dubiel
 */
public abstract class AbstractDialect implements Dialect {

    private final DialectTypes types = new DefaultDialectTypes();

    private final DialectConstraints constraints = new DefaultDialectConstraints();

    private final DefaultDialectQueries queries = new DefaultDialectQueries();

    @Override
    public boolean supportsSequences() {
        return true;
    }

    @Override
    public boolean supportsAttributeModifier(String modifier) {
        return false;
    }

    @Override
    public DialectTypes types() {
        return types;
    }

    @Override
    public Json json(String json) {
        return new BasicJson(json);
    }

    @Override
    public DialectConstraints constraints() {
        return constraints;
    }

    @Override
    public DialectQueries queries() {
        return queries;
    }

    @Override
    public String createRelationDefaultOptions() {
        return "";
    }

    @Override
    public boolean supportsNativeJsonColumnType() {
        return false;
    }
}
