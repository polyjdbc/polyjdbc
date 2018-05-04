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

import org.polyjdbc.core.dialect.Dialect;

import java.util.Arrays;

public abstract class Attribute implements SchemaPart {

    static final int TO_STRING_LENGTH = 100;

    private Dialect dialect;

    private String name;

    private boolean unique;

    private boolean notNull;

    private Object defaultValue;

    private String[] additionalModifiers;

    Attribute(Dialect dialect, String name) {
        this.dialect = dialect;
        this.name = name;
    }

    protected abstract String getTypeDefinition();

    protected Dialect dialect() {
        return dialect;
    }

    @Override
    public String toString() {
        return ddl();
    }

    @Override
    public String ddl() {
        StringBuilder builder = new StringBuilder(TO_STRING_LENGTH);
        builder.append(name).append(" ").append(getTypeDefinition()).append(" ");

        builder.append(dialect.constraints().attributeModifiers(unique, notNull, defaultValue)).append(" ");

        if (additionalModifiers != null) {
            for (String additionalModifier : additionalModifiers) {
                if (dialect.supportsAttributeModifier(additionalModifier)) {
                    builder.append(additionalModifier).append(" ");
                }
            }
        }
        return builder.toString().trim();
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isUnique() {
        return unique;
    }

    void unique() {
        this.unique = true;
    }

    void clearUniqueConstraint(){
        this.unique = false;
    }

    public boolean isNotNull() {
        return notNull;
    }

    void notNull() {
        this.notNull = true;
    }

    public String[] getAdditionalModifiers() {
        return Arrays.copyOf(additionalModifiers, additionalModifiers.length);
    }

    void withAdditionalModifiers(String... additionalModifiers) {
        this.additionalModifiers = Arrays.copyOf(additionalModifiers, additionalModifiers.length);
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    void withDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public static abstract class Builder<T, A extends Attribute> {

        private A attribute;

        private Relation.Builder parent;

        protected Builder(A attribute) {
            this.attribute = attribute;
        }

        protected Builder(A attribute, Relation.Builder parent) {
            this.attribute = attribute;
            this.parent = parent;
        }

        protected A attribute() {
            return attribute;
        }

        protected abstract T self();

        public Relation.Builder and() {
            parent.with(attribute);
            return parent;
        }

        public T unique() {
            attribute.unique();
            return self();
        }

        public T notNull() {
            attribute.notNull();
            return self();
        }

        public T withAdditionalModifiers(String... additionalModifiers) {
            attribute.withAdditionalModifiers(additionalModifiers);
            return self();
        }

        public T withDefaultValue(Object defaultValue) {
            attribute.withDefaultValue(defaultValue);
            return self();
        }

    }

}
