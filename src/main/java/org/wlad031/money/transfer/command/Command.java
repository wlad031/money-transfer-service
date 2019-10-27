package org.wlad031.money.transfer.command;

import lombok.NonNull;
import org.wlad031.money.transfer.model.Account;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;

public interface Command {

    void createNewAccount(@NonNull UUID accountId, @NonNull String name, @NonNull Currency currency);

    void createNewTransaction(
            @NonNull UUID transactionId,
            @NonNull UUID senderId, @NonNull UUID receiverId,
            @NonNull BigDecimal amountSent, @NonNull BigDecimal amountReceived,
            ZonedDateTime dateTime);

    void withdraw(
            @NonNull UUID transactionId,
            @NonNull UUID accountId, @NonNull BigDecimal amount,
            ZonedDateTime dateTime);

    void deposit(
            @NonNull UUID transactionId,
            @NonNull UUID accountId, @NonNull BigDecimal amount,
            ZonedDateTime dateTime);
}
