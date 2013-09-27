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

/**
 *
 * @author Adam Dubiel
 */
public class RelationBuilder {

    private Relation relation;

    private Heading heading;

    private Dialect dialect;

    private RelationBuilder(Dialect dialect, String name) {
        this.dialect = dialect;
        relation = new Relation(dialect, name);
        heading = new Heading(dialect);
    }

    public static RelationBuilder relation(Dialect dialect, String name) {
        return new RelationBuilder(dialect, name);
    }

    public Relation build() {
        relation.withHeading(heading);
        return relation;
    }

    public RelationBuilder with(Attribute attribute) {
        heading.addAttribute(attribute);
        return this;
    }

    public RelationBuilder constrainedBy(Constraint... constraints) {
        for(Constraint constraint : constraints) {
            relation.addConstraint(constraint);
        }
        return this;
    }

    public static LongAttributeBuilder longAttribute(Dialect dialect, String name) {
        return LongAttributeBuilder.longAttribute(dialect, name);
    }

}
