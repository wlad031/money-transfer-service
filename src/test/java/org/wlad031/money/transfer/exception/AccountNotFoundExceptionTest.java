package org.wlad031.money.transfer.exception;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class AccountNotFoundExceptionTest {

    @Test
    public void getMessage_ValidId() {
        final var id = UUID.randomUUID();
        final var ex = new AccountNotFoundException(id);
        assertEquals("Account ID " + id.toString() + " not found", ex.getMessage());
    }

    @Test(expected = NullPointerException.class)
    public void getMessage_NullId() {
        throw new AccountNotFoundException(null);
    }
}