package org.wlad031.money.transfer.validator;

import org.junit.Test;
import org.wlad031.money.transfer.exception.InvalidCurrencyException;
import org.wlad031.money.transfer.model.request.CreateNewAccountRequestBody;

import static org.junit.Assert.*;

public class AccountControllerValidatorTest {

    @Test
    public void validateCreateNewAccount_ValidRequest() {
        final var validator = new AccountControllerValidator();
        validator.validateCreateNewAccount(new CreateNewAccountRequestBody("name", "EUR"));
    }

    @Test(expected = InvalidCurrencyException.class)
    public void validateCreateNewAccount_InvalidCurrency() {
        final var validator = new AccountControllerValidator();
        validator.validateCreateNewAccount(new CreateNewAccountRequestBody("name", "LOL"));
    }
}