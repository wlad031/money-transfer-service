package org.wlad031.money.transfer.exception;

import lombok.NonNull;

import java.util.UUID;

public class NotEnoughBalanceException extends ValidationException {

    public NotEnoughBalanceException(@NonNull UUID accountId, @NonNull UUID transactionId) {
        super("Transaction " + transactionId.toString() +
                " not allowed: account=" + accountId.toString() +
                " has too low balance");
    }
}
