/*
 * Copyright 2013 Adam Dubiel.
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
package org.polyjdbc.core.test;

import java.util.Date;

/**
 *
 * @author Adam Dubiel
 */
public class TypeTestItem {

    String string;

    long longAttr;

    int integerAttr;

    boolean booleanAttr;

    char character;

    Date date;

    Date timestamp;

    String text;

    public TypeTestItem() {
    }

    public String getString() {
        return string;
    }

    public long getLongAttr() {
        return longAttr;
    }

    public int getIntegerAttr() {
        return integerAttr;
    }

    public boolean isBooleanAttr() {
        return booleanAttr;
    }

    public char getCharacter() {
        return character;
    }

    public Date getDate() {
        return date;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getText() {
        return text;
    }

}
