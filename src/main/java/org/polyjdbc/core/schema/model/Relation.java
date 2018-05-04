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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.util.StringUtils;

/**
 *
 * @author Adam Dubiel
 */
public class Relation implements SchemaEntity {

    private static final int TO_STRING_LENGTH_BASE = 30;

    private String name;

    private Dialect dialect;

    private Heading heading;

    private String schemaNameWithSeparator;

    private Collection<Constraint> constraints = new LinkedList<Constraint>();

    Relation(Dialect dialect, String name) {
        this(dialect, name, "");
    }

    Relation(Dialect dialect, String name, String schemaName) {
        this.dialect = dialect;
        this.name = name;
        if ((schemaName == null) || (schemaName.isEmpty())) {
            this.schemaNameWithSeparator = "";
        } else {
            this.schemaNameWithSeparator = schemaName + ".";
        }
    }

    @Override
    public String toString() {
        return ddl();
    }

    @Override
    public String ddl() {
        String headingDDL = heading.toString();
        String constraintsDDL = StringUtils.concatenate(",\n", constraints.toArray());
        StringBuilder builder = new StringBuilder(TO_STRING_LENGTH_BASE + headingDDL.length() + constraintsDDL.length());

        builder.append("CREATE TABLE ").append(schemaNameWithSeparator+name).append(" (\n")
                .append(headingDDL);
        if (constraintsDDL.length() > 0) {
            builder.append(",\n");
        }
        builder.append(constraintsDDL).append("\n) ")
                .append(dialect.createRelationDefaultOptions());

        return builder.toString();
    }

    @Override
    public String dropDDL() {
        return "DROP TABLE " + schemaNameWithSeparator+name;
    }

    public Dialect getDialect() {
        return dialect;
    }

    @Override
    public String getName() {
        return name;
    }

    public Heading getHeading() {
        return heading;
    }

    void withHeading(Heading heading) {
        this.heading = heading;
    }

    public Collection<Constraint> getConstraints() {
        return Collections.unmodifiableCollection(constraints);
    }

    void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    public static final class Builder {

        private Relation relation;

        private Schema schema;

        private Heading heading;

        private Dialect dialect;

        private Builder(Dialect dialect, String name) {
            this.dialect = dialect;
            relation = new Relation(dialect, name);
            heading = new Heading(dialect);
        }

        private Builder(Dialect dialect, String name, String schemaName) {
            this.dialect = dialect;
            relation = new Relation(dialect, name, schemaName);
            heading = new Heading(dialect);
        }

        private Builder(Schema schema, String name) {
            this(schema.getDialect(), name);
            this.schema = schema;
        }

        private Builder(Schema schema, String name, String schemaName) {
            this(schema.getDialect(), name, schemaName);
            this.schema = schema;
        }

        public static Builder relation(Dialect dialect, String name) {
            return new Builder(dialect, name);
        }

        public static Builder relation(Schema schema, String name) {
            return new Builder(schema, name);
        }

        public static Builder relation(Dialect dialect, String name, String schemaName) {
            return new Builder(dialect, name, schemaName);
        }

        public static Builder relation(Schema schema, String name, String schemaName) {
            return new Builder(schema, name, schemaName);
        }

        public Relation build() {
            relation.withHeading(heading);
            if (schema != null) {
                schema.add(relation);
            }
            return relation;
        }

        Builder with(Attribute attribute) {
            heading.addAttribute(attribute);
            return this;
        }

        Builder with(Constraint constraint) {
            relation.addConstraint(constraint);
            return this;
        }

        public Builder withAttribute() {
            return this;
        }

        public LongAttribute.Builder longAttr(String name) {
            return LongAttribute.Builder.longAttr(dialect, name, this);
        }

        public IntegerAttribute.Builder integer(String name) {
            return IntegerAttribute.Builder.integer(dialect, name, this);
        }

        public FloatAttribute.Builder floatAttr(String name) {
            return FloatAttribute.Builder.floatAttr(dialect, name, this);
        }

        public NumberAttribute.Builder number(String name) {
            return NumberAttribute.Builder.number(dialect, name, this);
        }

        public StringAttribute.Builder string(String name) {
            return StringAttribute.Builder.string(dialect, name, this);
        }

        public TextAttribute.Builder text(String name) {
            return TextAttribute.Builder.text(dialect, name, this);
        }

        public CharAttribute.Builder character(String name) {
            return CharAttribute.Builder.character(dialect, name, this);
        }

        public BooleanAttribute.Builder booleanAttr(String name) {
            return BooleanAttribute.Builder.booleanAttr(dialect, name, this);
        }

        public DateAttribute.Builder date(String name) {
            return DateAttribute.Builder.date(dialect, name, this);
        }

        public TimestampAttribute.Builder timestamp(String name) {
            return TimestampAttribute.Builder.timestamp(dialect, name, this);
        }

        public Builder constrainedBy() {
            return this;
        }

        public PrimaryKeyConstraint.Builder primaryKey(String name) {
            return PrimaryKeyConstraint.Builder.primaryKey(dialect, name, this);
        }

        public ForeignKeyConstraint.Builder foreignKey(String name) {
            return ForeignKeyConstraint.Builder.foreignKey(dialect, name, this);
        }

        Attribute getAttribute(String name){
            for (Attribute a : heading.getAttributes()){
                if (a.getName().equals(name)){
                    return a;
                }
            }
            return null;
        }

    }

}
