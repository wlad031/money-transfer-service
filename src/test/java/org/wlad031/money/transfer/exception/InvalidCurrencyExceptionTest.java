package org.wlad031.money.transfer.exception;

import org.junit.Test;

import static org.junit.Assert.*;

public class InvalidCurrencyExceptionTest {

    @Test
    public void getMessage_NotNullCurrency() {
        final var ex = new InvalidCurrencyException("hello");
        assertEquals("Invalid currency hello", ex.getMessage());
    }

    @Test
    public void getMessage_NullCurrency() {
        final var ex = new InvalidCurrencyException(null);
        assertEquals("Invalid currency null", ex.getMessage());
    }
}