package org.wlad031.money.transfer.dao;

import lombok.NonNull;
import org.wlad031.money.transfer.model.Transaction;

import java.util.Collection;
import java.util.UUID;

public interface TransactionDao {

    Transaction getById(@NonNull UUID id);
    @NonNull Collection<Transaction> getAccountTransactions(@NonNull UUID accountId);

    void create(@NonNull Transaction transaction);

    void completeTransaction(@NonNull UUID id);
    void abortTransaction(@NonNull UUID id);
}
