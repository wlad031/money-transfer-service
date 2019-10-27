package org.wlad031.money.transfer.dao;

import lombok.NonNull;
import org.wlad031.money.transfer.exception.NotEnoughBalanceException;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;

import java.util.Collection;
import java.util.UUID;

/**
 * DAO interface for {@link Account}-related operations
 */
public interface AccountDao {

    /**
     * Finds account by it's ID
     *
     * @param id account ID to search
     * @return found account, null if not found
     */
    Account getById(@NonNull UUID id);

    /**
     * Finds all saved accounts
     *
     * @return collection of saved accounts
     */
    @NonNull Collection<Account> getAll();

    /**
     * Saves newly created account to the system
     *
     * @param account account to be saved
     */
    void create(@NonNull Account account);

    /**
     * Updates accounts according to the newly created transaction.
     * Updates transaction sender and receiver accounts balances and lastUpdated times.
     *
     * @param transaction sender and receiver accounts of this transaction will be updated
     * @throws NotEnoughBalanceException in case if sender's balance is too low
     *                                   for sending given amount
     */
    void updateAccounts(@NonNull Transaction transaction);
}
