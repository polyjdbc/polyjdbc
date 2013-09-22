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
package org.polyjdbc.core.test.assertions;

import org.fest.assertions.api.AbstractAssert;
import org.fest.assertions.api.Assertions;
import org.polyjdbc.core.exception.PolyJdbcException;

/**
 *
 * @author Adam Dubiel
 */
public class PolyJdbcExceptionAssert extends AbstractAssert<PolyJdbcExceptionAssert, PolyJdbcException> {

    private PolyJdbcExceptionAssert(PolyJdbcException actual) {
        super(actual, PolyJdbcExceptionAssert.class);
    }

    public static PolyJdbcExceptionAssert assertThat(PolyJdbcException actual) {
        return new PolyJdbcExceptionAssert(actual);
    }

    public PolyJdbcExceptionAssert hasCode(String code) {
        Assertions.assertThat(actual.getCode()).isEqualTo(code);
        return this;
    }

}
