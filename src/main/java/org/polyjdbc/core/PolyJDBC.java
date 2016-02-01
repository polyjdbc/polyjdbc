/*
 * Copyright 2014 Adam Dubiel.
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
package org.polyjdbc.core;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.query.QueryFactory;
import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.query.SimpleQueryRunner;
import org.polyjdbc.core.query.TransactionRunner;
import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;

import java.io.Closeable;

public interface PolyJDBC {

    Dialect dialect();

    QueryFactory query();

    QueryRunner queryRunner();

    SimpleQueryRunner simpleQueryRunner();

    TransactionRunner transactionRunner();

    SchemaManager schemaManager();

    SchemaInspector schemaInspector();

    void rollback(QueryRunner... toRollback);

    void close(Closeable... toClose);
}
