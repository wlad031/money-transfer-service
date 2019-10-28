package org.wlad031.money.transfer.exception;

import lombok.NonNull;

import java.math.BigDecimal;

public class InvalidTransactionAmount extends ValidationException {

    public InvalidTransactionAmount(@NonNull String fieldName, BigDecimal amount) {
        super("Invalid amount=" + (amount == null ? "null" : amount.toString()) + " for field " + fieldName);
    }
}
