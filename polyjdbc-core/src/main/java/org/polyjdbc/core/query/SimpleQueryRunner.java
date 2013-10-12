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

    private TransactionRunner runner;

    public SimpleQueryRunner(TransactionManager transactionManager) {
        runner = new TransactionRunner(transactionManager);
    }

    public <T> T queryUnique(final SelectQuery query, final ObjectMapper<T> mapper) {
        return runner.run(new TransactionWrapper<T>() {
            public T perform(QueryRunner queryRunner) {
                return queryRunner.queryUnique(query, mapper);
            }
        });
    }

    public <T> List<T> queryList(final SelectQuery query, final ObjectMapper<T> mapper) {
        return runner.run(new TransactionWrapper<List<T>>() {
            public List<T> perform(QueryRunner queryRunner) {
                return queryRunner.queryList(query, mapper);
            }
        });
    }

    public <T> Set<T> querySet(final SelectQuery query, final ObjectMapper<T> mapper) {
        return runner.run(new TransactionWrapper<Set<T>>() {
            public Set<T> perform(QueryRunner queryRunner) {
                return queryRunner.querySet(query, mapper);
            }
        });
    }

    public boolean queryExistence(final SelectQuery query) {
        return runner.run(new TransactionWrapper<Boolean>() {
            public Boolean perform(QueryRunner queryRunner) {
                return queryRunner.queryExistence(query);
            }
        });
    }
}
