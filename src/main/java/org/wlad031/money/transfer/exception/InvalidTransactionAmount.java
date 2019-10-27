package org.wlad031.money.transfer.exception;

import lombok.NonNull;

import java.math.BigDecimal;

public class InvalidTransactionAmount extends ValidationException {

    public InvalidTransactionAmount(@NonNull String fieldName, @NonNull BigDecimal amount) {
        super("Invalid amount=" + amount.toString() + " for field " + fieldName);
    }
}
