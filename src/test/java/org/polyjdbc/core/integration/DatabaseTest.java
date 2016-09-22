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
package org.polyjdbc.core.integration;

import org.polyjdbc.core.infrastructure.PolyDatabaseTest;
import org.polyjdbc.core.test.DatabaseBuilder;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.util.Arrays;

public class DatabaseTest extends PolyDatabaseTest {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DatabaseTest.class);

    protected TestSchemaManager schemaManager;
    
    @Parameters({"dialect", "url", "user", "password", "schemaName"})
    @BeforeClass(alwaysRun = true)
    public void setUpDatabase(@Optional("H2") String dialectCode,
                              @Optional("jdbc:h2:mem:test") String url,
                              @Optional("polly") String user,
                              @Optional("polly") String password,
                              @Optional String schemaName) throws Exception
    {
        logger.info("staring tests with "+dialectCode+" database ...");

        super.createDatabase(dialectCode, url, user, password, schemaName);

        this.schemaManager = new TestSchemaManager(polyJDBC(), schemaName);
        schemaManager.createSchema();
    }

    @BeforeMethod(alwaysRun = true)
    public void cleanDatabase() {
        super.deleteFromRelations(Arrays.asList("test", "type_test"));
    }

    @AfterClass(alwaysRun = true)
    public void tearDownDatabase() throws Exception {
        super.dropDatabase();
    }

    protected DatabaseBuilder database() {
        return DatabaseBuilder.database(polyJDBC());
    }
    
    @Override
    protected void dropSchema() {
        schemaManager.dropSchema();
    }
}
