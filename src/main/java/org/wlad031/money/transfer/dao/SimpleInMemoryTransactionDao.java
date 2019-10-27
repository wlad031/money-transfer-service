package org.wlad031.money.transfer.dao;

import com.google.inject.Inject;
import lombok.NonNull;
import org.wlad031.money.transfer.model.Transaction;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class SimpleInMemoryTransactionDao implements TransactionDao {

    private final SimpleInMemoryDataSource dataSource;

    @Inject
    public SimpleInMemoryTransactionDao(SimpleInMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Transaction getById(@NonNull UUID id) {
        return dataSource.getTransactions().get(id);
    }

    @Override
    public @NonNull Collection<Transaction> getAccountTransactions(@NonNull UUID accountId) {
        return dataSource.getTransactions().values().stream()
                .filter(t -> accountId.equals(t.getSenderId()) || accountId.equals(t.getReceiverId()))
                .collect(Collectors.toSet());
    }

    @Override
    public void create(@NonNull Transaction transaction) {
        dataSource.getTransactions().put(transaction.getId(), transaction);
    }

    @Override
    public void completeTransaction(@NonNull UUID id) {
        final var transaction = dataSource.getTransactions().get(id);
        if (transaction != null) transaction.complete();
    }

    @Override
    public void abortTransaction(@NonNull UUID id) {
        final var transaction = dataSource.getTransactions().get(id);
        if (transaction != null) transaction.abort();
    }
}
