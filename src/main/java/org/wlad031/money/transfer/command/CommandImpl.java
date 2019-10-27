package org.wlad031.money.transfer.command;

import com.google.inject.Inject;
import lombok.NonNull;
import org.wlad031.money.transfer.dao.AccountDao;
import org.wlad031.money.transfer.dao.TransactionDao;
import org.wlad031.money.transfer.exception.AccountNotFoundException;
import org.wlad031.money.transfer.exception.NotEnoughBalanceException;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;
import org.wlad031.money.transfer.model.TransactionAmount;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Implementations for all system's 'C's (commands) from CQRS.
 */
public class CommandImpl implements Command {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    @Inject
    public CommandImpl(AccountDao accountDao, TransactionDao transactionDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> createNewAccount(
            @NonNull UUID accountId, @NonNull String name, @NonNull Currency currency) {
        accountDao.create(new Account(accountId, name, currency));
        return CompletableFuture.runAsync(() -> {}); // may be more useful in future
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CompletableFuture<Void> createNewTransaction(
            @NonNull UUID transactionId,
            UUID senderId, UUID receiverId,
            BigDecimal amountSent, BigDecimal amountReceived,
            ZonedDateTime dateTime) {
        TransactionAmount transactionAmountSent = null;
        TransactionAmount transactionAmountReceived = null;

        if (senderId != null) {
            final var sender = accountDao.getById(senderId);
            if (sender == null) {
                throw new AccountNotFoundException(senderId);
            }
            transactionAmountSent = new TransactionAmount(sender.getCurrency(), amountSent);
        }
        if (receiverId != null) {
            final var receiver = accountDao.getById(receiverId);
            if (receiver == null) {
                throw new AccountNotFoundException(receiverId);
            }
            transactionAmountReceived = new TransactionAmount(receiver.getCurrency(), amountReceived);
        }
        if (dateTime == null) dateTime = ZonedDateTime.now();
        final var transaction = new Transaction(
                transactionId,
                senderId, receiverId,
                transactionAmountSent,
                transactionAmountReceived,
                dateTime);
        transactionDao.create(transaction);
        return CompletableFuture.runAsync(() -> {
            try {
                accountDao.updateAccounts(transaction);
                transactionDao.completeTransaction(transaction.getId());
            } catch (NotEnoughBalanceException e) {
                transactionDao.abortTransaction(transaction.getId());
            }
        });
    }
}
