package org.wlad031.money.transfer.exception;

import lombok.NonNull;

import java.util.UUID;

public class TransactionNotFoundException extends ValidationException {

    public TransactionNotFoundException(@NonNull UUID id) {
        super("Transaction ID " + id.toString() + " not found");
    }
}
