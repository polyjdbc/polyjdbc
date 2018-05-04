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
public class ForeignKeyConstraint extends Constraint {

    private String targetAttribute;

    private String targetRelation;

    private String targetRelationAttribute;

    ForeignKeyConstraint(Dialect dialect, String name) {
        super(dialect, name);
    }

    @Override
    protected String getDefinition() {
        return dialect().constraints().foreignKey(getName(), targetAttribute, targetRelation, targetRelationAttribute);
    }

    void withTargetAttribute(String targetAttribute) {
        this.targetAttribute = targetAttribute;
    }

    void withTargetRelation(String targetRelation) {
        this.targetRelation = targetRelation;
    }

    void withTargetRelationAttribute(String targetRelationAttribute) {
        this.targetRelationAttribute = targetRelationAttribute;
    }

    public static final class Builder {

        private Relation.Builder parent;

        private ForeignKeyConstraint constraint;

        private Builder(Dialect dialect, String name, Relation.Builder parent) {
            this.constraint = new ForeignKeyConstraint(dialect, name);
            this.parent = parent;
        }

        public static Builder foreignKey(Dialect dialect, String name, Relation.Builder parent) {
            return new Builder(dialect, name, parent);
        }

        public Relation.Builder and() {
            parent.with(constraint);
            return parent;
        }

        public Builder on(String attribute) {
            constraint.withTargetAttribute(attribute);
            return this;
        }

        public Builder references(String relation, String attribute) {
            constraint.withTargetRelation(relation);
            constraint.withTargetRelationAttribute(attribute);
            return this;
        }

    }

}
