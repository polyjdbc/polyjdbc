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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Adam Dubiel
 */
public class DialectRegistry {

    private static final Map<String, Dialect> dialects = new HashMap<String, Dialect>();

    static {
        addDialect(new H2Dialect());
    }

    private DialectRegistry() {
    }

    private static void addDialect(Dialect dialect) {
        dialects.put(dialect.getCode(), dialect);
    }

    public static boolean hasDialect(String code) {
        return dialects.containsKey(code);
    }

    public static Dialect dialect(String code) {
        return dialects.get(code);
    }
}
