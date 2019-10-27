package org.wlad031.money.transfer.dao;

import lombok.NonNull;
import org.wlad031.money.transfer.model.Transaction;

import java.util.Collection;
import java.util.UUID;

/**
 * DAO interface for {@link Transaction}-related operations
 */
public interface TransactionDao {

    /**
     * Finds transaction by it's ID
     *
     * @param id transaction ID to search
     * @return found transaction, null if not found
     */
    Transaction getById(@NonNull UUID id);

    /**
     * Finds account's transactions by account ID
     *
     * @param accountId account ID to search
     * @return collection with all account's transactions
     */
    @NonNull Collection<Transaction> getAccountTransactions(@NonNull UUID accountId);

    /**
     * Saves newly created transaction to the system
     *
     * @param transaction transaction to be saved
     */
    void create(@NonNull Transaction transaction);

    /**
     * Sets status of the transaction to 'COMPLETED' and updates it's processedDateTime
     * @param id ID of the transaction to be updated
     */
    void completeTransaction(@NonNull UUID id);

    /**
     * Sets status of the transaction to 'ABORTED' and updates it's processedDateTime
     * @param id ID of the transaction to be updated
     */
    void abortTransaction(@NonNull UUID id);
}
