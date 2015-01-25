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
package org.polyjdbc.core.type;

import java.math.BigDecimal;
import java.util.Date;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.polyjdbc.core.exception.UnknownColumnTypeException;

public class ColumnTypeMapper {

    private final Map<Class<?>, SqlType> mappings = new HashMap<Class<?>, SqlType>();

    public ColumnTypeMapper() {
        registerDefaultMappings();
    }
    
    public ColumnTypeMapper(Map<Class<?>, SqlType> mappings) {
        this();
        this.mappings.putAll(mappings);
    }

    private void registerDefaultMappings() {
        mappings.put(String.class, new SqlType(Types.VARCHAR));
        mappings.put(Integer.class, new SqlType(Types.INTEGER));
        mappings.put(Long.class, new SqlType(Types.BIGINT));
        mappings.put(Float.class, new SqlType(Types.FLOAT));
        mappings.put(Character.class, new SqlType(Types.CHAR));
        mappings.put(Boolean.class, new SqlType(Types.BOOLEAN));
        mappings.put(Date.class, new SqlType(Types.DATE));
        mappings.put(BigDecimal.class, new SqlType(Types.NUMERIC));
        mappings.put(java.sql.Timestamp.class, new SqlType(Types.DATE));
        mappings.put(Timestamp.class, new SqlType(Types.TIMESTAMP));
    }
    
    public SqlType forClass(Class<?> objectClass) {
        for (Map.Entry<Class<?>, SqlType> entry : mappings.entrySet()) {
            if(entry.getKey().isAssignableFrom(objectClass)) {
                return entry.getValue();
            }
        }
        throw new UnknownColumnTypeException("Could not find column type matching class " + objectClass.getCanonicalName() + ". "
                + "Make sure class is assignable from any of supported classes.");
    }

    public SqlType forObject(Object object) {
        return forClass(object.getClass());
    }
}
