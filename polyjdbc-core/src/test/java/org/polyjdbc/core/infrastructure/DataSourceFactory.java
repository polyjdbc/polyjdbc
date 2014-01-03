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

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.polyjdbc.core.dialect.Dialect;

/**
 *
 * @author Adam Dubiel
 */
public final class DataSourceFactory {

    private static final Map<String, String> DIALECT_DRIVER_CLASS = new HashMap<String, String>();

    static {
        DIALECT_DRIVER_CLASS.put("H2", "org.h2.Driver");
        DIALECT_DRIVER_CLASS.put("POSTGRESQL", "org.postgresql.Driver");
        DIALECT_DRIVER_CLASS.put("MYSQL", "com.mysql.jdbc.Driver");
    }

    private DataSourceFactory() {
    }

    public static DataSource create(Dialect dialect, String databaseUrl, String user, String password) {
        BasicDataSource dataSource = new BasicDataSource();

        dataSource.setDriverClassName(DIALECT_DRIVER_CLASS.get(dialect.getCode()));
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setDefaultAutoCommit(false);

        return dataSource;
    }
}
