package org.wlad031.money.transfer.exception;

public class InvalidCurrencyException extends ValidationException {

    public InvalidCurrencyException(String currency) {
        super("Invalid currency " + (currency == null ? "null" : currency));
    }
}
