/*
 * Copyright 2014 Adam Dubiel.
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
package org.polyjdbc.core.util;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Adam Dubiel
 */
public class StringUtilsTest {

    @Test
    public void shouldReturnEmptyStringWhenEmptyValueArrayPassed() {
        // when
        String result = StringUtils.concatenate(";");

        // then
        assertThat(result).isEqualTo("");
    }

    @Test
    public void shouldConcatenateValuesUsingSeparatorWithoutLeavingTrailingSeparator() {
        // when
        String result = StringUtils.concatenate(";", "a", "b", "c");

        // then
        assertThat(result).isEqualTo("a;b;c");
    }

    @Test
    public void shoulduseToStringOnValuesToConcatenate() {
        // when
        String result = StringUtils.concatenate(";", "a", new SimpleObject(), "c");

        // then
        assertThat(result).isEqualTo("a;simpleObject;c");
    }

    @Test
    public void shouldPutEmptyStringinPlaceOfNullValue() {
        // when
        String result = StringUtils.concatenate(";", "a", null, "c");

        // then
        assertThat(result).isEqualTo("a;;c");
    }

    private static class SimpleObject {

        @Override
        public String toString() {
            return "simpleObject";
        }
    }
}
