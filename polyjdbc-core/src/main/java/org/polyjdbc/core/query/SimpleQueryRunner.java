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
import java.util.Set;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.transaction.TransactionManager;

/**
 *
 * @author Adam Dubiel
 */
public class SimpleQueryRunner {

    private TransactionManager transactionManager;

    public SimpleQueryRunner(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    private QueryRunner queryRunner() {
        return new TransactionalQueryRunner(transactionManager.openTransaction());
    }

    public <T> T queryUnique(SelectQuery query, ObjectMapper<T> mapper) {
        QueryRunner queryRunner = queryRunner();
        try {
            return queryRunner.queryUnique(query, mapper);
        } finally {
            queryRunner.close();
        }
    }

    public <T> List<T> queryList(SelectQuery query, ObjectMapper<T> mapper) {
        QueryRunner queryRunner = queryRunner();
        try {
            return queryRunner.queryList(query, mapper);
        } finally {
            queryRunner.close();
        }
    }

    public <T> Set<T> querySet(SelectQuery query, ObjectMapper<T> mapper) {
        QueryRunner queryRunner = queryRunner();
        try {
            return queryRunner.querySet(query, mapper);
        } finally {
            queryRunner.close();
        }
    }

    public boolean queryExistence(SelectQuery query) {
        QueryRunner queryRunner = queryRunner();
        try {
            return queryRunner.queryExistence(query);
        } finally {
            queryRunner.close();
        }
    }
}
