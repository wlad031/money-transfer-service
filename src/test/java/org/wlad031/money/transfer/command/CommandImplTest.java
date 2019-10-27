package org.wlad031.money.transfer.command;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.wlad031.money.transfer.AbstractTest;
import org.wlad031.money.transfer.dao.AccountDao;
import org.wlad031.money.transfer.dao.TransactionDao;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;
import org.wlad031.money.transfer.model.TransactionAmount;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.*;

public class CommandImplTest extends AbstractTest {

    private Command command;
    private AccountDao accountDao;
    private TransactionDao transactionDao;

    @Before
    public void setUp() throws Exception {
        accountDao = mock(AccountDao.class);
        transactionDao = mock(TransactionDao.class);
        command = new CommandImpl(accountDao, transactionDao);
    }

    @Test
    public void createNewAccount_ValidParams() throws ExecutionException, InterruptedException {
        final var accountId = UUID.randomUUID();
        final var future = command.createNewAccount(accountId, "name", Currency.getInstance("RUB"));
        verify(accountDao).create(argThat(new ArgumentMatcher<>() {
            @Override
            public boolean matches(Object argument) {
                final var a = (Account) argument;
                return a.getId().equals(accountId)
                        && a.getName().equals("name")
                        && a.getCurrency().equals(Currency.getInstance("RUB"));
            }
        }));
        future.get();
        verifyNoMoreInteractions(accountDao);
        verifyZeroInteractions(transactionDao);
    }

    @Test(expected = NullPointerException.class)
    public void createNewAccount_NullId() {
        command.createNewAccount(null, "name", Currency.getInstance("RUB"));
    }

    @Test(expected = NullPointerException.class)
    public void createNewAccount_NullName() {
        command.createNewAccount(UUID.randomUUID(), null, Currency.getInstance("RUB"));
    }

    @Test(expected = NullPointerException.class)
    public void createNewAccount_NullCurrency() {
        command.createNewAccount(UUID.randomUUID(), "name", null);
    }

    @Test
    public void createNewTransaction_ValidParams() throws ExecutionException, InterruptedException {
        final var transactionId = UUID.randomUUID();
        final var senderId = UUID.randomUUID();
        final var receiverId = UUID.randomUUID();
        final var now = ZonedDateTime.now();
        final var sender = new Account(senderId, "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        final var receiver = new Account(receiverId, "receiver", Currency.getInstance("RUB"));
        receiver.setBalance(new BigDecimal("100.0"));
        when(accountDao.getById(eq(senderId))).thenReturn(sender);
        when(accountDao.getById(eq(receiverId))).thenReturn(receiver);
        final var future = command.createNewTransaction(transactionId, senderId, receiverId,
                new BigDecimal("10.21"), new BigDecimal("10.11"), now);
        verify(accountDao, times(1)).getById(eq(senderId));
        verify(accountDao, times(1)).getById(eq(receiverId));
        verify(transactionDao).create(argThat(new ArgumentMatcher<>() {
            @Override
            public boolean matches(Object argument) {
                final var t = (Transaction) argument;
                return t.getId().equals(transactionId)
                        && t.getSenderId().equals(senderId)
                        && t.getReceiverId().equals(receiverId)
                        && t.getAmountSent().equals(new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")))
                        && t.getAmountReceived().equals(new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("10.11")))
                        && t.getCreatedDateTime().equals(now);
            }
        }));
        future.get();
        verify(accountDao, times(1)).updateAccounts(argThat(new ArgumentMatcher<>() {
            @Override
            public boolean matches(Object argument) {
                final var t = (Transaction) argument;
                return t.getId().equals(transactionId)
                        && t.getSenderId().equals(senderId)
                        && t.getReceiverId().equals(receiverId)
                        && t.getAmountSent().equals(new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")))
                        && t.getAmountReceived().equals(new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("10.11")))
                        && t.getCreatedDateTime().equals(now);
            }
        }));
        verify(transactionDao, times(1)).completeTransaction(eq(transactionId));
    }

    @Test
    public void createNewTransaction_SenderIdIsNull() throws ExecutionException, InterruptedException {
        final var transactionId = UUID.randomUUID();
        final var receiverId = UUID.randomUUID();
        final var now = ZonedDateTime.now();
        final var receiver = new Account(receiverId, "receiver", Currency.getInstance("RUB"));
        receiver.setBalance(new BigDecimal("100.0"));
        when(accountDao.getById(eq(receiverId))).thenReturn(receiver);
        final var future = command.createNewTransaction(transactionId, null, receiverId,
                null, new BigDecimal("10.11"), now);
        verify(accountDao, times(1)).getById(eq(receiverId));
        verify(transactionDao).create(argThat(new ArgumentMatcher<>() {
            @Override
            public boolean matches(Object argument) {
                final var t = (Transaction) argument;
                return t.getId().equals(transactionId)
                        && t.getSenderId() == null
                        && t.getReceiverId().equals(receiverId)
                        && t.getAmountSent() == null
                        && t.getAmountReceived().equals(new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("10.11")));
            }
        }));
        future.get();
        verify(accountDao, times(1)).updateAccounts(argThat(new ArgumentMatcher<>() {
            @Override
            public boolean matches(Object argument) {
                final var t = (Transaction) argument;
                return t.getId().equals(transactionId)
                        && t.getSenderId() == null
                        && t.getReceiverId().equals(receiverId)
                        && t.getAmountSent() == null
                        && t.getAmountReceived().equals(new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("10.11")));
            }
        }));
        verify(transactionDao, times(1)).completeTransaction(eq(transactionId));
    }

    @Test
    public void createNewTransaction_ReceiverIdIsNull() throws ExecutionException, InterruptedException {
        final var transactionId = UUID.randomUUID();
        final var senderId = UUID.randomUUID();
        final var now = ZonedDateTime.now();
        final var sender = new Account(senderId, "sender", Currency.getInstance("EUR"));
        sender.setBalance(new BigDecimal("100.0"));
        when(accountDao.getById(eq(senderId))).thenReturn(sender);
        final var future = command.createNewTransaction(transactionId, senderId, null,
                new BigDecimal("10.21"), null, now);
        verify(accountDao, times(1)).getById(eq(senderId));
        verify(transactionDao).create(argThat(new ArgumentMatcher<>() {
            @Override
            public boolean matches(Object argument) {
                final var t = (Transaction) argument;
                return t.getId().equals(transactionId)
                        && t.getSenderId().equals(senderId)
                        && t.getReceiverId() == null
                        && t.getAmountSent().equals(new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")))
                        && t.getAmountReceived() == null
                        && t.getCreatedDateTime().equals(now);
            }
        }));
        future.get();
        verify(accountDao, times(1)).updateAccounts(argThat(new ArgumentMatcher<>() {
            @Override
            public boolean matches(Object argument) {
                final var t = (Transaction) argument;
                return t.getId().equals(transactionId)
                        && t.getSenderId().equals(senderId)
                        && t.getReceiverId() == null
                        && t.getAmountSent().equals(new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.21")))
                        && t.getAmountReceived() == null
                        && t.getCreatedDateTime().equals(now);
            }
        }));
        verify(transactionDao, times(1)).completeTransaction(eq(transactionId));
    }
}