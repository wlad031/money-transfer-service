package org.wlad031.money.transfer.exception;

import lombok.NonNull;

import java.math.BigDecimal;

public class InvalidTransactionAmount extends ValidationException {

    private final String fieldName;
    private final BigDecimal amount;

    public InvalidTransactionAmount(@NonNull String fieldName, BigDecimal amount) {
        this.fieldName = fieldName;
        this.amount = amount;
    }

    @Override
    public String getMessage() {
        return "Invalid amount=" + amount.toString() + " for field " + fieldName;
    }
}
