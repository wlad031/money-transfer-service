package org.wlad031.money.transfer.command;

import lombok.NonNull;
import org.wlad031.money.transfer.model.Account;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;

public interface Command {

    void createNewAccount(
            @NonNull UUID accountId,
            @NonNull String name,
            @NonNull Currency currency);

    void createNewTransaction(
            @NonNull UUID transactionId,
            UUID senderId, UUID receiverId,
            BigDecimal amountSent, BigDecimal amountReceived,
            ZonedDateTime dateTime);
}
