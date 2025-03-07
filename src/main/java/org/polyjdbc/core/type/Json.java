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

package org.polyjdbc.core.type;

import org.polyjdbc.core.dialect.DialectCasts;

public class Json implements TypeWrapper {

    private final String value;

    private DialectCasts dialectCasts;

    public Json(String value, DialectCasts casts) {
        this.value = value;
        this.dialectCasts = casts;
    }

    @Override
    public Object value() {
        return value;
    }

    public String cast(String placeholder) {
        return this.dialectCasts.json(placeholder);
    }

}
