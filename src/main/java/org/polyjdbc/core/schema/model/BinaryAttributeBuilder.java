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

public final class BinaryAttributeBuilder extends AttributeBuilder<BinaryAttributeBuilder, BinaryAttribute> {

    private BinaryAttributeBuilder(Dialect dialect, String name, RelationBuilder parent) {
        super(new BinaryAttribute(dialect, name), parent);
    }

    public static BinaryAttributeBuilder binaryAttr(Dialect dialect, String name, RelationBuilder parent) {
        return new BinaryAttributeBuilder(dialect, name, parent);
    }

    @Override
    protected BinaryAttributeBuilder self() {
        return this;
    }

}
