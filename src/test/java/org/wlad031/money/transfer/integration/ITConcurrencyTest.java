package org.wlad031.money.transfer.integration;

import lombok.NonNull;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wlad031.money.transfer.command.Command;
import org.wlad031.money.transfer.command.CommandImpl;
import org.wlad031.money.transfer.dao.SimpleInMemoryAccountDao;
import org.wlad031.money.transfer.dao.SimpleInMemoryDataSource;
import org.wlad031.money.transfer.dao.SimpleInMemoryTransactionDao;
import org.wlad031.money.transfer.model.Transaction;
import org.wlad031.money.transfer.model.TransactionAmount;
import org.wlad031.money.transfer.query.Query;
import org.wlad031.money.transfer.query.QueryImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

public class ITConcurrencyTest extends AbstractIntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(ITConcurrencyTest.class);

    private static SimpleInMemoryDataSource dataSource;
    private static Query query;
    private static Command command;

    @BeforeClass
    public static void setUpClass() {
        dataSource = new SimpleInMemoryDataSource();
        final var accountDao = new SimpleInMemoryAccountDao(dataSource);
        final var transactionDao = new SimpleInMemoryTransactionDao(dataSource);
        query = new QueryImpl(accountDao, transactionDao);
        command = new CommandImpl(accountDao, transactionDao);
    }

    @After
    public void tearDown() {
        dataSource.getAccounts().clear();
        dataSource.getTransactions().clear();
    }

    @Test
    public void manyParallelTransactionsSubmitted() throws InterruptedException {
        final var executor = Executors.newFixedThreadPool(16);
        final var accountId1 = UUID.randomUUID();
        final var accountId2 = UUID.randomUUID();
        command.createNewAccount(accountId1, "name 1", Currency.getInstance("EUR"));
        command.createNewAccount(accountId2, "name 2", Currency.getInstance("EUR"));
        final var initBalance1 = new BigDecimal("1000000");
        final var initBalance2 = new BigDecimal("1000000");
        dataSource.getAccounts().get(accountId1).setBalance(initBalance1);
        dataSource.getAccounts().get(accountId2).setBalance(initBalance2);
        final var r = new Random();
        final var min = 1.0;
        final var max = 10000.0;
        final Supplier<BigDecimal> newAmount = () -> new BigDecimal(max + r.nextFloat() * (max - min)).setScale(2, RoundingMode.HALF_UP);
        final int steps = 10_000;
        final CompletableFuture[] futures = new CompletableFuture[steps * 2];
        for (int i = 0; i < steps; i++) {
            int finalI = i;
            CompletableFuture.runAsync(() -> {
                if (finalI % 1_000 == 0) LOGGER.info("Processed {} 1-2 transactions", finalI);
                final var amount = newAmount.get();
                futures[finalI] = command.createNewTransaction(UUID.randomUUID(), accountId1, accountId2, amount, amount, null);
            }, executor);
            CompletableFuture.runAsync(() -> {
                if (finalI % 1_000 == 0) LOGGER.info("Processed {} 2-1 transactions", finalI);
                final var amount = newAmount.get();
                futures[finalI + steps] = command.createNewTransaction(UUID.randomUUID(), accountId2, accountId1, amount, amount, null);
            }, executor);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        CompletableFuture.allOf(futures).join();
        final var amountReceived1 = dataSource.getTransactions().values().stream()
                .filter(t -> t.getStatus() == Transaction.Status.COMPLETED)
                .filter(t -> t.getReceiverId().equals(accountId1))
                .map(Transaction::getAmountSent)
                .map(TransactionAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final var amountReceived2 = dataSource.getTransactions().values().stream()
                .filter(t -> t.getStatus() == Transaction.Status.COMPLETED)
                .filter(t -> t.getReceiverId().equals(accountId2))
                .map(Transaction::getAmountSent)
                .map(TransactionAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final var amountSent1 = dataSource.getTransactions().values().stream()
                .filter(t -> t.getStatus() == Transaction.Status.COMPLETED)
                .filter(t -> t.getSenderId().equals(accountId1))
                .map(Transaction::getAmountSent)
                .map(TransactionAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final var amountSent2 = dataSource.getTransactions().values().stream()
                .filter(t -> t.getStatus() == Transaction.Status.COMPLETED)
                .filter(t -> t.getSenderId().equals(accountId2))
                .map(Transaction::getAmountSent)
                .map(TransactionAmount::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(initBalance1.add(amountReceived1).add(amountSent1.negate()), dataSource.getAccounts().get(accountId1).getBalance());
        assertEquals(initBalance2.add(amountReceived2).add(amountSent2.negate()), dataSource.getAccounts().get(accountId2).getBalance());
    }
}
