package org.wlad031.money.transfer.exception;

import lombok.NonNull;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {
    private final UUID id;

    public TransactionNotFoundException(@NonNull UUID id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Transaction ID " + id.toString() + " not found";
    }
}
