package org.wlad031.money.transfer.query;

import com.google.common.collect.Sets;
import lombok.NonNull;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.wlad031.money.transfer.dao.AccountDao;
import org.wlad031.money.transfer.dao.TransactionDao;
import org.wlad031.money.transfer.exception.AccountNotFoundException;
import org.wlad031.money.transfer.exception.TransactionNotFoundException;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;
import org.wlad031.money.transfer.model.TransactionAmount;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
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
        query.generateAccountId();
        verifyZeroInteractions(accountDao);
        verifyZeroInteractions(transactionDao);
    }

    @Test
    public void generateTransactionId() {
        query.generateTransactionId();
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

    @Test(expected = NullPointerException.class)
    public void getAccountDetailsById_NullAccount() throws ExecutionException, InterruptedException {
        query.getAccountDetailsById(null).get();
    }

    @Test(expected = AccountNotFoundException.class)
    public void getAccountDetailsById_NonExistingAccount() throws Throwable {
        final var id = UUID.randomUUID();
        when(accountDao.getById(eq(id))).thenReturn(null);
        try {
            query.getAccountDetailsById(id).get();
            fail("Call must fail, but it didn't");
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test
    public void getAvailableAccountIds_NoAccounts() throws ExecutionException, InterruptedException {
        final var actual = query.getAvailableAccountIds().get();
        verifyZeroInteractions(transactionDao);
        verify(accountDao, times(1)).getAll();
        verifyNoMoreInteractions(accountDao);
        assertNotNull(actual);
        assertThat(actual, empty());
    }

    @Test(expected = NullPointerException.class)
    public void getAccountTransactions_NullAccountId() {
        query.getAccountTransactions(null);
    }

    @Test
    public void getAccountTransactions_ExistingAccountId() throws ExecutionException, InterruptedException {
        final var account1 = new Account(UUID.randomUUID(), "name 1", Currency.getInstance("EUR"));
        final var account2 = new Account(UUID.randomUUID(), "name 2", Currency.getInstance("EUR"));
        final var transaction = new Transaction(UUID.randomUUID(), account1.getId(), account2.getId(),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.11")),
                ZonedDateTime.now());
        when(accountDao.getById(eq(account1.getId()))).thenReturn(account1);
        when(transactionDao.getAccountTransactions(eq(account1.getId()))).thenReturn(singleton(transaction));
        final var actual = query.getAccountTransactions(account1.getId()).get();
        verify(accountDao, times(1)).getById(eq(account1.getId()));
        verify(transactionDao, times(1)).getAccountTransactions(eq(account1.getId()));
        verifyNoMoreInteractions(accountDao);
        verifyNoMoreInteractions(transactionDao);
        assertNotNull(actual);
        assertThat(actual, hasSize(1));
        assertEquals(singleton(transaction), actual);
    }

    @Test(expected = AccountNotFoundException.class)
    public void getAccountTransactions_NonExistingAccountId() throws Throwable {
        final var id = UUID.randomUUID();
        when(accountDao.getById(eq(id))).thenReturn(null);
        try {
            query.getAccountTransactions(id).get();
            fail("Call must fail, but it didn't");
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = NullPointerException.class)
    public void getTransactionDetails_NullId() {
        query.getTransactionDetails(null);
    }

    @Test
    public void getTransactionDetails_ExistingTransaction() throws ExecutionException, InterruptedException {
        final var account1 = new Account(UUID.randomUUID(), "name 1", Currency.getInstance("EUR"));
        final var account2 = new Account(UUID.randomUUID(), "name 2", Currency.getInstance("EUR"));
        final var transaction = new Transaction(UUID.randomUUID(), account1.getId(), account2.getId(),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.11")),
                ZonedDateTime.now());
        when(transactionDao.getById(eq(transaction.getId()))).thenReturn(transaction);
        final var actual = query.getTransactionDetails(transaction.getId()).get();
        verify(transactionDao, times(1)).getById(eq(transaction.getId()));
        verifyNoMoreInteractions(transactionDao);
        verifyZeroInteractions(accountDao);
        assertEquals(transaction, actual);
    }

    @Test(expected = TransactionNotFoundException.class)
    public void getTransactionDetails_NonExistingTransaction() throws Throwable {
        final var id = UUID.randomUUID();
        when(transactionDao.getById(eq(id))).thenReturn(null);
        try {
            query.getTransactionDetails(id).get();
            fail("Call must fail, but it didn't");
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }
}