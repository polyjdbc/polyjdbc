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
package org.polyjdbc.core.query;

import org.polyjdbc.core.exception.PolyJdbcException;
import org.polyjdbc.core.transaction.Transaction;
import org.polyjdbc.core.transaction.TransactionManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.googlecode.catchexception.CatchException.catchException;
import static org.mockito.Mockito.*;

/**
 *
 * @author Adam Dubiel
 */
public class TransactionRunnerTest {

    private TransactionRunner transactionRunner;

    private TransactionManager transactionManager;

    @BeforeMethod
    public void setUp() {
        transactionManager = mock(TransactionManager.class);
        transactionRunner = new TransactionRunner(transactionManager);
    }

    @Test
    public void shouldCreateQueryRunnerAndCloseItAfterPerformingTransaction() {
        // given
        Transaction transaction = mock(Transaction.class);
        when(transactionManager.openTransaction()).thenReturn(transaction);

        // when
        transactionRunner.run(new VoidTransactionWrapper() {
            @Override
            public void performVoid(QueryRunner queryRunner) {
            }
        });

        // then
        verify(transactionManager).openTransaction();
        verify(transaction).commit();
        verify(transaction).closeWithArtifacts();
    }

    @Test
    public void shouldCloseTransactionEvenWhenExceptionWasThrown() {
        // given
        Transaction transaction = mock(Transaction.class);
        when(transactionManager.openTransaction()).thenReturn(transaction);

        // when
        catchException(transactionRunner).run(new VoidTransactionWrapper() {
            @Override
            public void performVoid(QueryRunner queryRunner) {
                throw new PolyJdbcException("", "");
            }
        });

        // then
        verify(transaction).commit();
        verify(transaction).closeWithArtifacts();
    }
}