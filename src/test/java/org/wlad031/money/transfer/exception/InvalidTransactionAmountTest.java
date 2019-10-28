package org.wlad031.money.transfer.exception;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class InvalidTransactionAmountTest {

    @Test
    public void getMessage_NotNullAmount() {
        final var ex = new InvalidTransactionAmount("fieldName", new BigDecimal("-100.00"));
        assertEquals("Invalid amount=-100.00 for field fieldName", ex.getMessage());
    }

    @Test(expected = NullPointerException.class)
    public void getMessage_NullField() {
        throw new InvalidTransactionAmount(null, BigDecimal.ZERO);
    }

    @Test
    public void getMessage_NullAmount() {
        final var ex = new InvalidTransactionAmount("fieldName", null);
        assertEquals("Invalid amount=null for field fieldName", ex.getMessage());
    }
}