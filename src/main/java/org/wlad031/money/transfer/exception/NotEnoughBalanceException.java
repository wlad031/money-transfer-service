package org.wlad031.money.transfer.exception;

import lombok.NonNull;

import java.util.UUID;

public class NotEnoughBalanceException extends RuntimeException {

    private final UUID accountId;
    private final UUID transactionId;

    public NotEnoughBalanceException(@NonNull UUID accountId, @NonNull UUID transactionId) {
        this.accountId = accountId;
        this.transactionId = transactionId;
    }

    @Override
    public String getMessage() {
        return "Transaction " + transactionId.toString() + " not allowed: account=" + accountId.toString() +
                " has too low balance";
    }
}
