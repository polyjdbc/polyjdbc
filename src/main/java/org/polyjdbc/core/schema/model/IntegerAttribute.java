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

public class IntegerAttribute extends Attribute {

    private int integerPrecision;

    public IntegerAttribute(Dialect dialect, String name) {
        super(dialect, name);
    }

    @Override
    protected String getTypeDefinition() {
        return dialect().types().integer(integerPrecision);
    }

    void withIntegerPrecision(int integerPrecision) {
        this.integerPrecision = integerPrecision;
    }

    public static final class Builder extends Attribute.Builder<Builder, IntegerAttribute> {

        private Builder(Dialect dialect, String name, Relation.Builder parent) {
            super(new IntegerAttribute(dialect, name), parent);
        }

        public static Builder integer(Dialect dialect, String name, Relation.Builder parent) {
            return new Builder(dialect, name, parent);
        }

        @Override
        protected Builder self() {
            return this;
        }

        public Builder withIntegerPrecision(int integerPrecision) {
            attribute().withIntegerPrecision(integerPrecision);
            return this;
        }

    }

}
