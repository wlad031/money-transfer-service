package org.wlad031.money.transfer.exception;

import org.junit.Test;

import static org.junit.Assert.*;

public class InvalidAccountNameExceptionTest {

    @Test
    public void getMessage_NullName() {
        final var ex = new InvalidAccountNameException(null);
        assertEquals("Invalid account name: null", ex.getMessage());
    }

    @Test
    public void getMessage_ValidName() {
        final var ex = new InvalidAccountNameException("hello");
        assertEquals("Invalid account name: hello", ex.getMessage());
    }
}