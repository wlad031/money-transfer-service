package org.wlad031.money.transfer.exception;

import org.junit.Test;
import org.wlad031.money.transfer.model.Transaction;

import java.util.UUID;

import static org.junit.Assert.*;

public class TransactionNotFoundExceptionTest {

    @Test
    public void getMessage_ValidId() {
        final var id = UUID.randomUUID();
        final var ex = new TransactionNotFoundException(id);
        assertEquals("Transaction ID " + id.toString() + " not found", ex.getMessage());
    }

    @Test(expected = NullPointerException.class)
    public void getMessage_NullId() {
        throw new TransactionNotFoundException(null);
    }
}