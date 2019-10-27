package org.wlad031.money.transfer.command;

import lombok.NonNull;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * The interface for all system's 'C's (commands) from CQRS.
 */
public interface Command {

    /**
     * Creates and saves new account
     *
     * @param accountId The account ID
     * @param name      The account name
     * @param currency  The currency of the account
     * @return empty completable future that finishes wher all operations are completed
     * @see Account
     */
    CompletableFuture<Void> createNewAccount(
            @NonNull UUID accountId,
            @NonNull String name,
            @NonNull Currency currency);

    /**
     * Creates and saves new transaction
     *
     * @param transactionId  The transaction ID.
     * @param senderId       ID of the transaction's sender account.
     *                       If null, transaction considered as a 'deposit' - transaction
     *                       without any sender.
     * @param receiverId     ID of the transaction's receiver account.
     *                       If null, transaction considered as a 'withdrawal' - transaction
     *                       without any receiver.
     * @param amountSent     The amount that will be sent from the sender account.
     *                       Could be null in case if senderId is null.
     * @param amountReceived The amount that will be received by the receiver account.
     *                       Could be null in case if receiverId is null.
     * @param dateTime       The datetime when transaction created. If null,
     *                       datetime will be generated as ZonedDateTime.now.
     * @return empty completable future that finishes wher all operations are completed
     * @see Transaction
     * @see ZonedDateTime#now()
     */
    CompletableFuture<Void> createNewTransaction(
            @NonNull UUID transactionId,
            UUID senderId, UUID receiverId,
            BigDecimal amountSent, BigDecimal amountReceived,
            ZonedDateTime dateTime);

    /**
     * Creates and saves new 'deposit' transaction - transaction without any sender.
     *
     * @param transactionId  The transaction ID.
     * @param receiverId     ID of the transaction's receiver account.
     *                       If null, transaction considered as a 'withdrawal' - transaction
     *                       without any receiver.
     * @param amountReceived The amount that will be received by the receiver account.
     *                       Could be null in case if receiverId is null.
     * @param dateTime       The datetime when transaction created. If null,
     *                       datetime will be generated as ZonedDateTime.now.
     * @return empty completable future that finishes wher all operations are completed
     * @see #createNewTransaction(UUID, UUID, UUID, BigDecimal, BigDecimal, ZonedDateTime)
     * @see Transaction
     * @see ZonedDateTime#now()
     */
    default CompletableFuture<Void> createDepositTransaction(
            @NonNull UUID transactionId,
            @NonNull UUID receiverId, @NonNull BigDecimal amountReceived,
            ZonedDateTime dateTime) {
        return createNewTransaction(transactionId, null, receiverId, null, amountReceived, dateTime);
    }

    /**
     * Creates and saves new 'withdrawal' transaction - transaction without any receiver.
     *
     * @param transactionId The transaction ID.
     * @param senderId      ID of the transaction's sender account.
     *                      If null, transaction considered as a 'deposit' - transaction
     *                      without any sender.
     * @param amountSent    The amount that will be sent from the sender account.
     *                      Could be null in case if senderId is null.
     * @param dateTime      The datetime when transaction created. If null,
     *                      datetime will be generated as ZonedDateTime.now.
     * @return empty completable future that finishes wher all operations are completed
     * @see #createNewTransaction(UUID, UUID, UUID, BigDecimal, BigDecimal, ZonedDateTime)
     * @see Transaction
     * @see ZonedDateTime#now()
     */
    default CompletableFuture<Void> createWithdrawalTransaction(
            @NonNull UUID transactionId,
            @NonNull UUID senderId, @NonNull BigDecimal amountSent,
            ZonedDateTime dateTime) {
        return createNewTransaction(transactionId, senderId, null, amountSent, null, dateTime);
    }
}
