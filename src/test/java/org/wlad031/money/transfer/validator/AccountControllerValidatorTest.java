package org.wlad031.money.transfer.validator;

import org.junit.Test;
import org.wlad031.money.transfer.exception.InvalidAccountNameException;
import org.wlad031.money.transfer.exception.InvalidCurrencyException;
import org.wlad031.money.transfer.model.request.CreateNewAccountRequestBody;

import static org.junit.Assert.*;

public class AccountControllerValidatorTest {

    private final AccountControllerValidator validator = new AccountControllerValidator();

    @Test
    public void validateCreateNewAccount_ValidRequest() {
        validator.validateCreateNewAccount(new CreateNewAccountRequestBody("name", "EUR"));
    }

    @Test(expected = InvalidAccountNameException.class)
    public void validateCreateNewAccount_InvalidName() {
        validator.validateCreateNewAccount(new CreateNewAccountRequestBody(null, "EUR"));
    }

    @Test(expected = InvalidCurrencyException.class)
    public void validateCreateNewAccount_InvalidCurrency() {
        validator.validateCreateNewAccount(new CreateNewAccountRequestBody("name", "LOL"));
    }

    @Test(expected = NullPointerException.class)
    public void validateCreateNewAccount_NullBody() {
        validator.validateCreateNewAccount(null);
    }
}