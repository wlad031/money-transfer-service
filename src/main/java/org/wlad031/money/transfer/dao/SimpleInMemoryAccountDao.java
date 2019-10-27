package org.wlad031.money.transfer.dao;

import com.google.inject.Inject;
import lombok.NonNull;
import org.wlad031.money.transfer.exception.NotEnoughBalanceException;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;

import java.util.Collection;
import java.util.UUID;

/**
 * Simple implementation of {@link AccountDao} that uses underlying maps.
 */
public class SimpleInMemoryAccountDao implements AccountDao {

    private final SimpleInMemoryDataSource dataSource;

    @Inject
    public SimpleInMemoryAccountDao(SimpleInMemoryDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Account getById(@NonNull UUID id) {
        return dataSource.getAccounts().get(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull public Collection<Account> getAll() {
        return dataSource.getAccounts().values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void create(@NonNull Account account) {
        dataSource.getAccounts().put(account.getId(), account);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAccounts(@NonNull Transaction transaction) {
        if (transaction.getSenderId() != null) {
            final var sender = dataSource.getAccounts().get(transaction.getSenderId());
            if (sender.getBalance().compareTo(transaction.getAmountSent().getAmount()) < 0) {
                throw new NotEnoughBalanceException(transaction.getSenderId(), transaction.getId());
            }
            sender.add(transaction.getAmountSent().getAmount().negate());
        }
        if (transaction.getReceiverId() != null) {
            final var receiver = dataSource.getAccounts().get(transaction.getReceiverId());
            receiver.add(transaction.getAmountReceived().getAmount());
        }
    }
}
