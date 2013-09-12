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

/**
 *
 * @author Adam Dubiel
 */
public class DeleteQuery {

    private Query query;

    DeleteQuery() {
        this.query = new Query();
    }

    Query build() {
        query.compile();
        return query;
    }

    public DeleteQuery from(String tableName) {
        query.append("delete from ").append(tableName);
        return this;
    }

    public DeleteQuery where(String conditions) {
        query.append("where ").append(conditions);
        return this;
    }

    public DeleteQuery withArgument(String argumentName, Object object) {
        query.setArgument(argumentName, object);
        return this;
    }
}
