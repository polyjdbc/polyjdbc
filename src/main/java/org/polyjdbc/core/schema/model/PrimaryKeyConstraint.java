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

/**
 *
 * @author Adam Dubiel
 */
public class PrimaryKeyConstraint extends Constraint {

    private String[] targetAttributes;

    PrimaryKeyConstraint(Dialect dialect, String name) {
        super(dialect, name);
    }

    @Override
    protected String getDefinition() {
        return dialect().constraints().primaryKey(getName(), targetAttributes);
    }

    void withTargetAttributes(String[] targetAttributes) {
        if (targetAttributes != null) {
            this.targetAttributes = Arrays.copyOf(targetAttributes, targetAttributes.length);
        }
    }

    String getFirstAttributeName(){
        return targetAttributes[0];
    }

    boolean hasOneAttribute(){
        return targetAttributes != null && targetAttributes.length == 1;
    }

    public static final class Builder {

        private Relation.Builder parent;

        private PrimaryKeyConstraint constraint;

        private Builder(Dialect dialect, String name, Relation.Builder parent) {
            this.constraint = new PrimaryKeyConstraint(dialect, name);
            this.parent = parent;
        }

        public static Builder primaryKey(Dialect dialect, String name, Relation.Builder parent) {
            return new Builder(dialect, name, parent);
        }

        public Relation.Builder and() {
            if (constraint.hasOneAttribute()){
                Attribute attr = parent.getAttribute(constraint.getFirstAttributeName());
                if (attr != null) {
                    attr.clearUniqueConstraint();
                }
            }

            parent.with(constraint);
            return parent;
        }

        public Builder using(String... attributes) {
            constraint.withTargetAttributes(attributes);
            return this;
        }

    }

}
