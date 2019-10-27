package org.wlad031.money.transfer.exception;

import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.*;

public class AccountNotFoundExceptionTest {

    @Test
    public void getMessage() {
        final var id = UUID.randomUUID();
        final var ex = new AccountNotFoundException(id);
        assertEquals("Account ID " + id.toString() + " not found", ex.getMessage());
    }
}