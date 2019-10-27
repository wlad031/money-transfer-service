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

public class CommandImpl implements Command {

    private final AccountDao accountDao;
    private final TransactionDao transactionDao;

    @Inject
    public CommandImpl(AccountDao accountDao, TransactionDao transactionDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
    }

    @Override
    public void createNewAccount(
            @NonNull UUID accountId, @NonNull String name, @NonNull Currency currency) {
        accountDao.create(new Account(accountId, name, currency));
    }

    @Override
    public void createNewTransaction(
            @NonNull UUID transactionId,
            @NonNull UUID senderId, @NonNull UUID receiverId,
            @NonNull BigDecimal amountSent, @NonNull BigDecimal amountReceived,
            ZonedDateTime dateTime) {
        final var sender = accountDao.getById(senderId);
        if (sender == null) {
            throw new AccountNotFoundException(senderId);
        }
        final var receiver = accountDao.getById(receiverId);
        if (receiver == null) {
            throw new AccountNotFoundException(senderId);
        }
        if (dateTime == null) dateTime = ZonedDateTime.now();
        final var transaction = new Transaction(transactionId,
                senderId, receiverId,
                new TransactionAmount(sender.getCurrency(),  amountSent),
                new TransactionAmount(receiver.getCurrency(), amountReceived), dateTime);
        transactionDao.create(transaction);
        CompletableFuture.runAsync(() -> {
            try {
                accountDao.updateAccounts(transaction);
            } catch (NotEnoughBalanceException e) {
                transactionDao.abortTransaction(transaction.getId());
            }
            transactionDao.completeTransaction(transaction.getId());
        });
    }

    @Override
    public void withdraw(@NonNull UUID transactionId, @NonNull UUID accountId, @NonNull BigDecimal amount, ZonedDateTime dateTime) {
        final var account = accountDao.getById(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }
        if (dateTime == null) dateTime = ZonedDateTime.now();
        final var transaction = Transaction.withdraw(transactionId, accountId,
                new TransactionAmount(account.getCurrency(), amount), dateTime);
        CompletableFuture.runAsync(() -> {
            try {
                accountDao.updateAccounts(transaction);
            } catch (NotEnoughBalanceException e) {
                transactionDao.abortTransaction(transaction.getId());
            }
            transactionDao.completeTransaction(transaction.getId());
        });
    }

    @Override
    public void deposit(@NonNull UUID transactionId, @NonNull UUID accountId, @NonNull BigDecimal amount, ZonedDateTime dateTime) {
        final var account = accountDao.getById(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }
        if (dateTime == null) dateTime = ZonedDateTime.now();
        final var transaction = Transaction.deposit(transactionId, accountId,
                new TransactionAmount(account.getCurrency(), amount), dateTime);
        CompletableFuture.runAsync(() -> {
            try {
                accountDao.updateAccounts(transaction);
            } catch (NotEnoughBalanceException e) {
                transactionDao.abortTransaction(transaction.getId());
            }
            transactionDao.completeTransaction(transaction.getId());
        });
    }
}
