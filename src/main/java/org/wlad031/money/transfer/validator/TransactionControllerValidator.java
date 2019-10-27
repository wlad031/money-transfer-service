package org.wlad031.money.transfer.validator;

import io.javalin.http.Context;
import lombok.NonNull;
import org.wlad031.money.transfer.controller.TransactionController;
import org.wlad031.money.transfer.exception.InvalidIdException;
import org.wlad031.money.transfer.exception.InvalidTransactionAmount;
import org.wlad031.money.transfer.exception.ValidationException;
import org.wlad031.money.transfer.model.request.CreateNewTransactionRequestBody;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Validator for {@link TransactionController}'s methods
 */
public class TransactionControllerValidator extends AbstractValidator {

    /**
     * Checks request body of {@link TransactionController#createTransaction(Context)}
     *
     * @param body request body to check
     */
    public void validateCreateTransaction(@NonNull CreateNewTransactionRequestBody body) {
        if (body.getSenderId() == null && body.getReceiverId() == null) {
            throw new ValidationException("senderId and/or receiverId must be not null");
        }
        if (body.getSenderId() != null && body.getAmountSent() == null) {
            throw new ValidationException("senderId is not null, but amountSent is null");
        }
        if (body.getSenderId() == null && body.getAmountSent() != null) {
            throw new ValidationException("senderId is null, but amountSent is not null");
        }
        if (body.getReceiverId() != null && body.getAmountReceived() == null) {
            throw new ValidationException("receiverId is not null, but amountReceived is null");
        }
        if (body.getReceiverId() == null && body.getAmountReceived() != null) {
            throw new ValidationException("receiverId is null, but amountReceived is not null");
        }

        if (body.getSenderId() != null) {
            validateNotNullableId("senderId", body.getSenderId());
            if (body.getAmountSent().compareTo(BigDecimal.ZERO) < 1) {
                throw new InvalidTransactionAmount("amountSent", body.getAmountSent());
            }
        }
        if (body.getReceiverId() != null) {
            validateNotNullableId("receiverId", body.getReceiverId());
            if (body.getAmountReceived().compareTo(BigDecimal.ZERO) < 1) {
                throw new InvalidTransactionAmount("amountReceived", body.getAmountReceived());
            }
        }
    }
}
