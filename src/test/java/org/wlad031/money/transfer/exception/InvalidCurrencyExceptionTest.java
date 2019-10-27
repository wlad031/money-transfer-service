package org.wlad031.money.transfer.exception;

import org.junit.Test;

import static org.junit.Assert.*;

public class InvalidCurrencyExceptionTest {

    @Test
    public void getMessage() {
        final var ex = new InvalidCurrencyException("hello");
        assertEquals("Invalid currency hello", ex.getMessage());
    }
}