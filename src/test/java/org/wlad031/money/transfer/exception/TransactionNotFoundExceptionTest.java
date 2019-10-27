package org.wlad031.money.transfer.exception;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class TransactionNotFoundExceptionTest {

    @Test
    public void getMessage() {
        final var id = UUID.randomUUID();
        final var ex = new TransactionNotFoundException(id);
        assertEquals("Transaction ID " + id.toString() + " not found", ex.getMessage());
    }
}