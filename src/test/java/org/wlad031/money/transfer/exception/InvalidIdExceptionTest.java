package org.wlad031.money.transfer.exception;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class InvalidIdExceptionTest {

    @Test
    public void getMessage_NotNullId() {
        final var ex = new InvalidIdException("fieldName", "hello");
        assertEquals("Invalid ID=hello for field fieldName", ex.getMessage());
    }

    @Test(expected = NullPointerException.class)
    public void getMessage_NullField() {
        throw new InvalidIdException(null, "hello");
    }

    @Test
    public void getMessage_NullId() {
        final var ex = new InvalidIdException("fieldName", null);
        assertEquals("Null ID for field fieldName", ex.getMessage());
    }
}