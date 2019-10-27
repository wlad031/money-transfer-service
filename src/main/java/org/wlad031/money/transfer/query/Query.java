package org.wlad031.money.transfer.query;

import lombok.NonNull;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Query {

    @NonNull UUID generateAccountId();
    @NonNull UUID generateTransactionId();

    @NonNull CompletableFuture<Account> getAccountDetailsById(@NonNull UUID id);
    @NonNull CompletableFuture<Collection<UUID>> getAvailableAccountIds();

    @NonNull CompletableFuture<Collection<Transaction>> getAccountTransactions(@NonNull UUID id);
    @NonNull CompletableFuture<Transaction> getTransactionDetails(@NonNull UUID id);
}
