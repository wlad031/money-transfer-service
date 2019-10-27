package org.wlad031.money.transfer.query;

import lombok.NonNull;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The interface for all system's 'Q's (queries) from CQRS.
 */
public interface Query {

    /**
     * Generates new account ID
     *
     * @return generated ID
     * @see Account
     */
    @NonNull UUID generateAccountId();

    /**
     * Generates new transaction ID
     *
     * @return generated ID
     * @see Transaction
     */
    @NonNull UUID generateTransactionId();

    /**
     * Finds the account by it's ID
     *
     * @param id ID for searching
     * @return CompletableFuture with found account (with null if account not found)
     * @see Account
     * @see CompletableFuture
     */
    @NonNull CompletableFuture<Account> getAccountDetailsById(@NonNull UUID id);

    /**
     * Finds all saved account IDs
     *
     * @return CompletableFuture with collection of all found account IDs
     * @see Account
     * @see CompletableFuture
     */
    @NonNull CompletableFuture<Collection<UUID>> getAvailableAccountIds();

    /**
     * Finds all account's transactions (sender/receiver).
     *
     * @param id ID of the account to find it's transactions
     * @return CompletableFuture with collection of all account's transaction IDs
     * @see Account
     * @see Transaction
     * @see CompletableFuture
     */
    @NonNull CompletableFuture<Collection<Transaction>> getAccountTransactions(@NonNull UUID id);

    /**
     * Finds the transaction by it's ID
     *
     * @param id ID for searching
     * @return CompletableFuture with found transaction (with null if account not found)
     * @see Transaction
     * @see CompletableFuture
     */
    @NonNull CompletableFuture<Transaction> getTransactionDetails(@NonNull UUID id);
}
