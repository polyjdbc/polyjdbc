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

/**
 *
 * @author Adam Dubiel
 */
public class OracleDialect extends AbstractDialect {

    private DialectTypes types = new OracleDialectTypes();

    private DialectQueries queries = new OracleDialectQueries();

    private DialectConstraints constraints = new OracleDialectConstraints();

    public String getCode() {
        return DialectRegistry.ORACLE.name();
    }

    @Override
    public String nextFromSequence(String sequenceName) {
        return "SELECT " + sequenceName + ".nextval FROM dual";
    }

    @Override
    public DialectTypes types() {
        return types;
    }

    @Override
    public DialectQueries queries() {
        return queries;
    }

    @Override
    public DialectConstraints constraints() {
        return constraints;
    }
}
