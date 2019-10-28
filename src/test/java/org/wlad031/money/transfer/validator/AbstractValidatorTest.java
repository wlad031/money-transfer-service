package org.wlad031.money.transfer.validator;

import org.junit.Test;
import org.wlad031.money.transfer.exception.InvalidIdException;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class AbstractValidatorTest {

    @Test(expected = NullPointerException.class)
    public void validateNotNullableId_NullFieldName() {
        AbstractValidator.validateNotNullableId(null, UUID.randomUUID().toString());
    }

    @Test
    public void validateNotNullableId_NullId() {
        try {
            AbstractValidator.validateNotNullableId("hello", null);
            fail("No exceptions thrown");
        } catch (InvalidIdException e) {
            assertEquals("Null ID for field hello", e.getMessage());
        }
    }

    @Test
    public void validateNotNullableId_NotValidId() {
        try {
            AbstractValidator.validateNotNullableId("hello", "world");
            fail("No exceptions thrown");
        } catch (InvalidIdException e) {
            assertEquals("Invalid ID=world for field hello", e.getMessage());
        }
    }

    @Test
    public void validateNotNullableId_ValidId() {
        AbstractValidator.validateNotNullableId("hello", UUID.randomUUID().toString());
    }
}
