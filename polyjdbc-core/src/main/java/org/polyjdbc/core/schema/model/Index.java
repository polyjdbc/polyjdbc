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
package org.polyjdbc.core.schema.model;

import java.util.Arrays;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.util.StringUtils;

/**
 *
 * @author Adam Dubiel
 */
public class Index implements SchemaPart {

    private String name;

    private String targetRelation;

    private String[] targetAttributes;

    Index(Dialect dialect, String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return ddl();
    }

    public String ddl() {
        StringBuilder builder = new StringBuilder(50);
        builder.append("CREATE INDEX ").append(name).append(" ON ").append(targetRelation).append("(")
                .append(StringUtils.concatenate(",", (Object[]) targetAttributes)).append(")");
        return builder.toString();
    }

    public String getTargetRelation() {
        return targetRelation;
    }

    void withTargetRelation(String targetRelation) {
        this.targetRelation = targetRelation;
    }

    public String[] getTargetAttributes() {
        return Arrays.copyOf(targetAttributes, targetAttributes.length);
    }

    void withTargetAttributes(String[] targetAttributes) {
        this.targetAttributes = Arrays.copyOf(targetAttributes, targetAttributes.length);
    }
}
