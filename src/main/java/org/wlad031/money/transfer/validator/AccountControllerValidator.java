package org.wlad031.money.transfer.validator;

import io.javalin.http.Context;
import lombok.NonNull;
import org.wlad031.money.transfer.controller.AccountController;
import org.wlad031.money.transfer.exception.InvalidAccountNameException;
import org.wlad031.money.transfer.exception.InvalidCurrencyException;
import org.wlad031.money.transfer.model.request.CreateNewAccountRequestBody;

import java.util.Currency;

/**
 * Validator for {@link AccountController}'s methods
 */
public class AccountControllerValidator extends AbstractValidator {

    /**
     * Checks request body of {@link AccountController#createNewAccount(Context)}
     *
     * @param body request body to check
     */
    public void validateCreateNewAccount(@NonNull CreateNewAccountRequestBody body) {
        if (body.getName() == null) {
            throw new InvalidAccountNameException(body.getName());
        }
        try {
            Currency.getInstance(body.getCurrency());
        } catch (RuntimeException e) {
            throw new InvalidCurrencyException(body.getCurrency());
        }
    }
}
