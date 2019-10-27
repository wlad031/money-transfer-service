package org.wlad031.money.transfer.validator;

import lombok.NonNull;
import org.wlad031.money.transfer.exception.InvalidAccountNameException;
import org.wlad031.money.transfer.exception.InvalidCurrencyException;
import org.wlad031.money.transfer.model.request.CreateNewAccountRequestBody;

import java.util.Currency;

public class AccountControllerValidator extends AbstractValidator {

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
