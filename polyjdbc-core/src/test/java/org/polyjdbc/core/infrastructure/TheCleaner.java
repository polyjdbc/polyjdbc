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
package org.polyjdbc.core.infrastructure;

import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.QueryRunner;

import java.util.Arrays;
import java.util.List;

public class TheCleaner {

    private final PolyJDBC polyJDBC;

    public TheCleaner(PolyJDBC polyJDBC) {
        this.polyJDBC = polyJDBC;
    }

    public void cleanDB(String... entities) {
        cleanDB(Arrays.asList(entities));
    }

    public void cleanDB(List<String> entities) {
        QueryRunner runner = null;
        try {
            runner = polyJDBC.queryRunner();
            for (String table : entities) {
                runner.delete(polyJDBC.query().delete().from(table));
            }
            runner.commit();
        } finally {
            polyJDBC.close(runner);
        }
    }
}
