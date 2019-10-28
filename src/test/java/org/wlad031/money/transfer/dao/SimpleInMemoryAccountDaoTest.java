package org.wlad031.money.transfer.dao;

import org.junit.Before;
import org.junit.Test;
import org.wlad031.money.transfer.exception.NotEnoughBalanceException;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;
import org.wlad031.money.transfer.model.TransactionAmount;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.empty;
import static org.junit.Assert.*;

public class SimpleInMemoryAccountDaoTest {

    private SimpleInMemoryDataSource dataSource;
    private AccountDao dao;

    @Before
    public void setUp() {
        dataSource = new SimpleInMemoryDataSource();
        dao = new SimpleInMemoryAccountDao(dataSource);
    }

    @Test
    public void getById_ExistingAccount() {
        final var id = UUID.randomUUID();
        final var expected = new Account(id, "name", Currency.getInstance("EUR"));
        dataSource.getAccounts().put(id, expected);

        final var actual = dao.getById(id);

        assertEquals(expected, actual);
    }

    @Test
    public void getById_NonExistingAccount() {
        final var id = UUID.randomUUID();
        dataSource.getAccounts().put(id, new Account(id, "name", Currency.getInstance("EUR")));

        final var actual = dao.getById(UUID.randomUUID());

        assertNull(actual);
    }

    @Test(expected = NullPointerException.class)
    public void getById_NullId() {
        dao.getById(null);
    }

    @Test
    public void getAll() {
        final var ids = IntStream.range(0, 10)
                .mapToObj(__ -> UUID.randomUUID())
                .collect(Collectors.toSet());
        final var expected =
                ids.stream()
                        .map(id -> new Account(id, "name", Currency.getInstance("RUB")))
                        .collect(Collectors.toSet());
        expected.forEach(a -> dataSource.getAccounts().put(a.getId(), a));

        final var actual = dao.getAll();

        assertEquals(expected, new HashSet<>(actual));
    }

    @Test
    public void getAll_EmptyDatasource() {
        final var actual = dao.getAll();

        assertNotNull(actual);
        assertThat(actual, empty());
    }

    @Test
    public void create() {
        final var id = UUID.randomUUID();
        final var account = new Account(id, "name", Currency.getInstance("RUB"));

        dao.create(account);

        assertEquals(account, dataSource.getAccounts().get(id));
    }

    @Test(expected = NullPointerException.class)
    public void create_Null() {
        dao.create(null);
    }

    @Test(expected = NullPointerException.class)
    public void updateAccounts_NullTransaction() {
        dao.updateAccounts(null);
    }

    @Test
    public void updateAccounts_NormalTransaction() {
        final var id1 = UUID.randomUUID();
        final var id2 = UUID.randomUUID();

        final var account1 = new Account(id1, "name", Currency.getInstance("RUB"));
        account1.setBalance(new BigDecimal("100.00"));
        final var account2 = new Account(id2, "name", Currency.getInstance("EUR"));
        account2.setBalance(new BigDecimal("100.00"));

        dataSource.getAccounts().put(account1.getId(), account1);
        dataSource.getAccounts().put(account2.getId(), account2);

        final var transactionId = UUID.randomUUID();

        dao.updateAccounts(new Transaction(transactionId, id1, id2,
                new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("100.00")),
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("1.4")),
                ZonedDateTime.now()));

        assertEquals(new BigDecimal("0").compareTo(account1.getBalance()), 0);
        assertEquals(new BigDecimal("101.4").compareTo(account2.getBalance()), 0);
    }

    @Test
    public void updateAccounts_DepositTransaction() {
        final var id = UUID.randomUUID();
        final var account = new Account(id, "name", Currency.getInstance("EUR"));
        account.setBalance(new BigDecimal("100.00"));

        dataSource.getAccounts().put(account.getId(), account);

        final var transactionId = UUID.randomUUID();

        dao.updateAccounts(Transaction.deposit(transactionId, id,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("1.4")),
                ZonedDateTime.now()));

        assertEquals(new BigDecimal("101.4").compareTo(account.getBalance()), 0);
    }

    @Test
    public void updateAccounts_WithdrawalTransaction() {
        final var id = UUID.randomUUID();
        final var account = new Account(id, "name", Currency.getInstance("EUR"));
        account.setBalance(new BigDecimal("100.00"));

        dataSource.getAccounts().put(account.getId(), account);

        final var transactionId = UUID.randomUUID();

        dao.updateAccounts(Transaction.withdraw(transactionId, id,
                new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("1.4")),
                ZonedDateTime.now()));

        assertEquals(new BigDecimal("98.6").compareTo(account.getBalance()), 0);
    }

    @Test
    public void updateAccounts_NotEnoughBalance() {
        final var id1 = UUID.randomUUID();
        final var id2 = UUID.randomUUID();

        final var account1 = new Account(id1, "name", Currency.getInstance("RUB"));
        account1.setBalance(new BigDecimal("100.00"));
        final var account2 = new Account(id2, "name", Currency.getInstance("EUR"));
        account2.setBalance(new BigDecimal("100.00"));

        dataSource.getAccounts().put(account1.getId(), account1);
        dataSource.getAccounts().put(account2.getId(), account2);

        final var transactionId = UUID.randomUUID();

        try {
            dao.updateAccounts(new Transaction(transactionId, id1, id2,
                    new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("101.00")),
                    new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("1.4")),
                    ZonedDateTime.now()));
            fail("No exceptions thrown");
        } catch (NotEnoughBalanceException e) {
            assertEquals("Transaction " + transactionId.toString() +
                    " not allowed: account=" + id1.toString() +
                    " has too low balance", e.getMessage());
        } finally {
            assertEquals(new BigDecimal("100.0").compareTo(account1.getBalance()), 0);
            assertEquals(new BigDecimal("100.0").compareTo(account2.getBalance()), 0);
        }
    }
}