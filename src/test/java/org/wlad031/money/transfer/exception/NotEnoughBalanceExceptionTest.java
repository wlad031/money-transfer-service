package org.wlad031.money.transfer.exception;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;

public class NotEnoughBalanceExceptionTest {

    @Test
    public void getMessage_ValidIds() {
        final var accountId = UUID.randomUUID();
        final var transactionId = UUID.randomUUID();
        final var ex = new NotEnoughBalanceException(accountId, transactionId);
        assertEquals("Transaction " + transactionId.toString() + " not allowed: account=" + accountId.toString() +
                " has too low balance", ex.getMessage());
    }

    @Test(expected = NullPointerException.class)
    public void getMessage_NullAccountId() {
        throw new NotEnoughBalanceException(null, UUID.randomUUID());
    }

    @Test(expected = NullPointerException.class)
    public void getMessage_NullTransactionId() {
        throw new NotEnoughBalanceException(UUID.randomUUID(), null);
    }
}