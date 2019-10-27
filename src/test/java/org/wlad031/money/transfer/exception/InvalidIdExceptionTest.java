package org.wlad031.money.transfer.exception;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class InvalidIdExceptionTest {

    @Test
    public void getMessage() {
        final var ex = new InvalidIdException("fieldName", "hello");
        assertEquals("Invalid ID=hello for field fieldName", ex.getMessage());
    }
}