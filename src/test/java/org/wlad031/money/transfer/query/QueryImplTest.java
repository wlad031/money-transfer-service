package org.wlad031.money.transfer.query;

import lombok.NonNull;
import org.junit.Before;
import org.junit.Test;
import org.wlad031.money.transfer.dao.AccountDao;
import org.wlad031.money.transfer.dao.TransactionDao;
import org.wlad031.money.transfer.exception.AccountNotFoundException;
import org.wlad031.money.transfer.model.Account;

import java.util.Currency;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class QueryImplTest {

    private Query query;
    private AccountDao accountDao;
    private TransactionDao transactionDao;

    @Before
    public void setUp() throws Exception {
        accountDao = mock(AccountDao.class);
        transactionDao = mock(TransactionDao.class);
        query = new QueryImpl(accountDao, transactionDao);
    }

    @Test
    public void generateAccountId() {
        final var id = query.generateAccountId();
        verifyZeroInteractions(accountDao);
        verifyZeroInteractions(transactionDao);
    }

    @Test
    public void generateTransactionId() {
        final var id = query.generateTransactionId();
        verifyZeroInteractions(accountDao);
        verifyZeroInteractions(transactionDao);
    }

    @Test
    public void getAccountDetailsById_ExistingAccount() throws ExecutionException, InterruptedException {
        final var id = UUID.randomUUID();
        final var account = new Account(id, "name", Currency.getInstance("EUR"));
        when(accountDao.getById(eq(id))).thenReturn(account);
        final var actual = query.getAccountDetailsById(id).get();
        verifyZeroInteractions(transactionDao);
        verify(accountDao, times(1)).getById(eq(id));
        verifyNoMoreInteractions(accountDao);
        assertEquals(account, actual);
    }

    @Test
    public void getAccountDetailsById_NonExistingAccount() throws ExecutionException, InterruptedException {
        final var id = UUID.randomUUID();
        when(accountDao.getById(eq(id))).thenReturn(null);
        try {
            final var actual = query.getAccountDetailsById(id).get();
            fail("Call must fail, but it didn't");
        } catch (ExecutionException e) {
            assertEquals(AccountNotFoundException.class, e.getCause().getClass());
            verifyZeroInteractions(transactionDao);
            verify(accountDao, times(1)).getById(eq(id));
            verifyNoMoreInteractions(accountDao);
        }
    }

    @Test
    public void getAvailableAccountIds() {
    }

    @Test
    public void getAccountTransactions() {
    }

    @Test
    public void getTransactionDetails() {
    }
}