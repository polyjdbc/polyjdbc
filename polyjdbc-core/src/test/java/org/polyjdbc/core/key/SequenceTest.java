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
package org.polyjdbc.core.key;

import org.polyjdbc.core.exception.SequenceLimitReachedException;
import org.testng.annotations.Test;
import static com.googlecode.catchexception.CatchException.*;
import static org.fest.assertions.api.Assertions.*;

/**
 *
 * @author Adam Dubiel
 */
public class SequenceTest {

    @Test
    public void shouldCreateNewSequenceThatIsMarkedForRecalculation() {
        // given
        Sequence sequence = new Sequence("test", 100);

        // when
        boolean recalculationNeeded = sequence.recalculationNeeded();

        // then
        assertThat(recalculationNeeded).isTrue();
    }

    @Test
    public void shouldBeReadyToUseAfterRecalculation() {
        // given
        Sequence sequence = new Sequence("test", 1);
        sequence.recalculate(1);

        // when
        boolean recalculationNeeded = sequence.recalculationNeeded();

        // then
        assertThat(recalculationNeeded).isFalse();
    }

    @Test
    public void shouldAssignNewLimitAndCurrentValuesAfterRecalculation() {
        // given
        Sequence sequence = new Sequence("test", 1);
        sequence.recalculate(1);

        // when
        long sequenceValue = sequence.nextValue();

        // then
        assertThat(sequenceValue).isEqualTo(1);
    }

    @Test
    public void shouldNeedRecalculationAfterAllocatedSequenceLimitIsUsed() {
        // given
        Sequence sequence = new Sequence("test", 1);
        sequence.recalculate(1);

        // when
        sequence.nextValue();

        // then
        assertThat(sequence.recalculationNeeded()).isTrue();
    }

    @Test
    public void shouldThrowExceptionWhenTryingToGetNextValueWhenRecalculationIsNeeded() {
        // given
        Sequence sequence = new Sequence("test", 1);

        // when
        catchException(sequence).nextValue();

        // then
        assertThat(caughtException()).isNotNull().isInstanceOf(SequenceLimitReachedException.class);
    }
}