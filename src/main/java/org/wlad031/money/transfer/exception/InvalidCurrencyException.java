package org.wlad031.money.transfer.exception;

public class InvalidCurrencyException extends ValidationException {

    private final String currency;

    public InvalidCurrencyException(String currency) {
        this.currency = currency;
    }

    @Override
    public String getMessage() {
        return "Invalid currency " + currency;
    }
}
