package org.wlad031.money.transfer.validator;

import lombok.NonNull;
import org.wlad031.money.transfer.exception.InvalidIdException;
import org.wlad031.money.transfer.exception.InvalidTransactionAmount;
import org.wlad031.money.transfer.model.request.CreateNewTransactionRequestBody;
import org.wlad031.money.transfer.model.request.DepositRequestBody;
import org.wlad031.money.transfer.model.request.WithdrawRequestBody;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionControllerValidator extends AbstractValidator {

    public void validateCreateTransaction(@NonNull CreateNewTransactionRequestBody body) {
        try {
            UUID.fromString(body.getSenderId());
        } catch (IllegalArgumentException e) {
            throw new InvalidIdException("senderId", body.getSenderId());
        }
        try {
            UUID.fromString(body.getReceiverId());
        } catch (IllegalArgumentException e) {
            throw new InvalidIdException("receiverId", body.getReceiverId());
        }
        if (body.getAmountSent().compareTo(BigDecimal.ZERO) < 1) {
            throw new InvalidTransactionAmount("amountSent", body.getAmountSent());
        }
        if (body.getAmountReceived().compareTo(BigDecimal.ZERO) < 1) {
            throw new InvalidTransactionAmount("amountReceived", body.getAmountReceived());
        }
    }

    public void validateWithdraw(@NonNull WithdrawRequestBody body) {
        try {
            UUID.fromString(body.getAccountId());
        } catch (IllegalArgumentException e) {
            throw new InvalidIdException("senderId", body.getAccountId());
        }
        if (body.getAmount().compareTo(BigDecimal.ZERO) < 1) {
            throw new InvalidTransactionAmount("amountSent", body.getAmount());
        }
    }

    public void validateDeposit(@NonNull DepositRequestBody body) {
        try {
            UUID.fromString(body.getAccountId());
        } catch (IllegalArgumentException e) {
            throw new InvalidIdException("senderId", body.getAccountId());
        }
        if (body.getAmount().compareTo(BigDecimal.ZERO) < 1) {
            throw new InvalidTransactionAmount("amountSent", body.getAmount());
        }
    }
}
