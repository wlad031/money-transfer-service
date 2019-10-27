package org.wlad031.money.transfer.dao;

import com.google.inject.Singleton;
import lombok.NonNull;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple datasource that uses underlying maps
 */
@Singleton
public class SimpleInMemoryDataSource {

    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();
    private final Map<UUID, Transaction> transactions = new ConcurrentHashMap<>();

    public @NonNull Map<UUID, Account> getAccounts() {
        return accounts;
    }

    public @NonNull Map<UUID, Transaction> getTransactions() {
        return transactions;
    }
}
