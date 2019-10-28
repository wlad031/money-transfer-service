package org.wlad031.money.transfer.dao;

import com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;
import org.wlad031.money.transfer.model.TransactionAmount;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class SimpleInMemoryTransactionDaoTest {

    private SimpleInMemoryDataSource dataSource;
    private TransactionDao dao;

    @Before
    public void setUp() {
        dataSource = new SimpleInMemoryDataSource();
        dao = new SimpleInMemoryTransactionDao(dataSource);
    }

    @Test
    public void getById_ExistingAccount() {
        final var id = UUID.randomUUID();
        final var senderId = UUID.randomUUID();
        final var receiverId = UUID.randomUUID();
        final var now = ZonedDateTime.now();

        final var expected = new Transaction(id, senderId, receiverId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                now);
        dataSource.getTransactions().put(id, expected);

        final var actual = dao.getById(id);

        assertEquals(expected, actual);
    }

    @Test
    public void getById_NonExistingAccount() {
        final var id = UUID.randomUUID();
        final var senderId = UUID.randomUUID();
        final var receiverId = UUID.randomUUID();
        final var now = ZonedDateTime.now();

        dataSource.getTransactions().put(id, new Transaction(id, senderId, receiverId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                now));

        final var actual = dao.getById(UUID.randomUUID());

        assertNull(actual);
    }

    @Test(expected = NullPointerException.class)
    public void getById_NullAccountId() {
        dao.getById(null);
    }

    @Test(expected = NullPointerException.class)
    public void getAccountTransactions_NullAccount_Id() {
        dao.getAccountTransactions(null);
    }

    @Test
    public void getAccountTransactions() {
        final var accountId = UUID.randomUUID();

        final var now = ZonedDateTime.now();

        // AccountId is sender and receiver
        final var transaction1 = new Transaction(UUID.randomUUID(), accountId, accountId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")), now);
        // AccountId is sender
        final var transaction2 = new Transaction(UUID.randomUUID(), accountId, UUID.randomUUID(),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")), now);
        // AccountId is receiver
        final var transaction3 = new Transaction(UUID.randomUUID(), UUID.randomUUID(), accountId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")), now);
        // AccountId not sender nor receiver
        final var transaction4 = new Transaction(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")), now);
        // AccountId is sender, receiver is null
        final var transaction5 = Transaction.withdraw(UUID.randomUUID(), accountId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")), now);
        // AccountId is receiver, sender is null
        final var transaction6 = Transaction.deposit(UUID.randomUUID(), accountId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")), now);

        dataSource.getTransactions().put(transaction1.getId(), transaction1);
        dataSource.getTransactions().put(transaction2.getId(), transaction2);
        dataSource.getTransactions().put(transaction3.getId(), transaction3);
        dataSource.getTransactions().put(transaction4.getId(), transaction4);
        dataSource.getTransactions().put(transaction5.getId(), transaction5);
        dataSource.getTransactions().put(transaction6.getId(), transaction6);

        final var actual = dao.getAccountTransactions(accountId);

        assertEquals(Sets.newHashSet(transaction1, transaction2, transaction3, transaction5, transaction6),
                new HashSet<>(actual));
    }

    @Test(expected = NullPointerException.class)
    public void create_Null() {
        dao.create(null);
    }

    @Test
    public void create() {
        final var id = UUID.randomUUID();
        final var senderId = UUID.randomUUID();
        final var receiverId = UUID.randomUUID();
        final var now = ZonedDateTime.now();

        final var transaction = new Transaction(id, senderId, receiverId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                now);
        dao.create(transaction);

        assertEquals(transaction, dataSource.getTransactions().get(id));
    }

    @Test(expected = NullPointerException.class)
    public void completeTransaction_NullTransaction() {
        dao.completeTransaction(null);
    }

    @Test
    public void completeTransaction_ExistingTransaction() {
        final var id = UUID.randomUUID();
        final var senderId = UUID.randomUUID();
        final var receiverId = UUID.randomUUID();
        final var now = ZonedDateTime.now();

        final var transaction = new Transaction(id, senderId, receiverId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                now);
        dataSource.getTransactions().put(id, transaction);

        assertEquals(Transaction.Status.PENDING, dao.getById(id).getStatus());

        dao.completeTransaction(id);

        assertEquals(Transaction.Status.COMPLETED, dao.getById(id).getStatus());
    }

    @Test
    public void completeTransaction_NonExistingTransaction() {
        final var id = UUID.randomUUID();
        final var senderId = UUID.randomUUID();
        final var receiverId = UUID.randomUUID();
        final var now = ZonedDateTime.now();

        final var transaction = new Transaction(id, senderId, receiverId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                now);
        dataSource.getTransactions().put(id, transaction);

        assertEquals(Transaction.Status.PENDING, dao.getById(id).getStatus());

        dao.completeTransaction(UUID.randomUUID());

        assertThat(dataSource.getTransactions().entrySet(), hasSize(1));
        assertEquals(Transaction.Status.PENDING, dao.getById(id).getStatus());
    }

    @Test(expected = NullPointerException.class)
    public void abortTransaction_NullTransaction() {
        dao.abortTransaction(null);
    }

    @Test
    public void abortTransaction_ExistingTransaction() {
        final var id = UUID.randomUUID();
        final var senderId = UUID.randomUUID();
        final var receiverId = UUID.randomUUID();
        final var now = ZonedDateTime.now();

        final var transaction = new Transaction(id, senderId, receiverId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                now);
        dataSource.getTransactions().put(id, transaction);

        assertEquals(Transaction.Status.PENDING, dao.getById(id).getStatus());

        dao.abortTransaction(id);

        assertEquals(Transaction.Status.ABORTED, dao.getById(id).getStatus());
    }

    @Test
    public void abortTransaction_NonExistingTransaction() {
        final var id = UUID.randomUUID();
        final var senderId = UUID.randomUUID();
        final var receiverId = UUID.randomUUID();
        final var now = ZonedDateTime.now();

        final var transaction = new Transaction(id, senderId, receiverId,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")),
                now);
        dataSource.getTransactions().put(id, transaction);

        assertEquals(Transaction.Status.PENDING, dao.getById(id).getStatus());

        dao.abortTransaction(UUID.randomUUID());

        assertThat(dataSource.getTransactions().entrySet(), hasSize(1));
        assertEquals(Transaction.Status.PENDING, dao.getById(id).getStatus());
    }
}