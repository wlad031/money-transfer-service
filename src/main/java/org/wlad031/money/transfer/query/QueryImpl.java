package org.wlad031.money.transfer.query;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.NonNull;
import org.wlad031.money.transfer.dao.AccountDao;
import org.wlad031.money.transfer.dao.TransactionDao;
import org.wlad031.money.transfer.exception.AccountNotFoundException;
import org.wlad031.money.transfer.exception.TransactionNotFoundException;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Implementations for all system's 'Q's (queries) from CQRS.
 */
@Singleton
public class QueryImpl implements Query {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    @Inject
    public QueryImpl(AccountDao accountDao, TransactionDao transactionDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull UUID generateAccountId() {
        return UUID.randomUUID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull UUID generateTransactionId() {
        return UUID.randomUUID();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull CompletableFuture<Account> getAccountDetailsById(@NonNull UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            final var account = accountDao.getById(id);
            if (account == null) {
                throw new AccountNotFoundException(id);
            }
            return account;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull CompletableFuture<Collection<UUID>> getAvailableAccountIds() {
        return CompletableFuture.supplyAsync(() -> {
            return accountDao.getAll().stream().map(Account::getId).collect(Collectors.toSet());
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull CompletableFuture<Collection<Transaction>> getAccountTransactions(@NonNull UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            final var account = accountDao.getById(id);
            if (account == null) {
                throw new AccountNotFoundException(id);
            }
            return transactionDao.getAccountTransactions(id);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public @NonNull CompletableFuture<Transaction> getTransactionDetails(@NonNull UUID id) {
        return CompletableFuture.supplyAsync(() -> {
            final var transaction = transactionDao.getById(id);
            if (transaction == null) {
                throw new TransactionNotFoundException(id);
            }
            return transaction;
        });
    }
}
