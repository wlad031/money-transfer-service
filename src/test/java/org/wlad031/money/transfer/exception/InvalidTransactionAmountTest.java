package org.wlad031.money.transfer.exception;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class InvalidTransactionAmountTest {

    @Test
    public void getMessage() {
        final var ex = new InvalidTransactionAmount("fieldName", new BigDecimal("-100.00"));
        assertEquals("Invalid amount=-100.00 for field fieldName", ex.getMessage());
    }
}