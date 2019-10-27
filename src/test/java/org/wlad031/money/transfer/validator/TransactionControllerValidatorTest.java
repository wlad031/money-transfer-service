package org.wlad031.money.transfer.validator;

import org.junit.Test;
import org.wlad031.money.transfer.exception.InvalidIdException;
import org.wlad031.money.transfer.exception.InvalidTransactionAmount;
import org.wlad031.money.transfer.model.request.CreateNewTransactionRequestBody;
import org.wlad031.money.transfer.model.request.DepositRequestBody;
import org.wlad031.money.transfer.model.request.WithdrawRequestBody;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.*;

public class TransactionControllerValidatorTest {

    @Test
    public void validateCreateTransaction_ValidRequest() {
        final var validator = new TransactionControllerValidator();

        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new BigDecimal("10.21"), new BigDecimal("10.01")));
    }

    @Test(expected = InvalidIdException.class)
    public void validateCreateTransaction_InvalidSenderId() {
        final var validator = new TransactionControllerValidator();

        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                "lol", UUID.randomUUID().toString(),
                new BigDecimal("10.21"), new BigDecimal("10.01")));
    }

    @Test(expected = InvalidIdException.class)
    public void validateCreateTransaction_InvalidReceiverId() {
        final var validator = new TransactionControllerValidator();

        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), "lol",
                new BigDecimal("10.21"), new BigDecimal("10.01")));
    }

    @Test(expected = InvalidTransactionAmount.class)
    public void validateCreateTransaction_InvalidAmountSent() {
        final var validator = new TransactionControllerValidator();

        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new BigDecimal("-10.21"), new BigDecimal("10.01")));
    }

    @Test(expected = InvalidTransactionAmount.class)
    public void validateCreateTransaction_InvalidAmountReceived() {
        final var validator = new TransactionControllerValidator();

        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new BigDecimal("10.21"), new BigDecimal("-10.01")));
    }

    @Test
    public void validateWithdraw_ValidRequest() {
        final var validator = new TransactionControllerValidator();

        validator.validateWithdraw(new WithdrawRequestBody(
                UUID.randomUUID().toString(),
                new BigDecimal("10.21")));
    }

    @Test(expected = InvalidIdException.class)
    public void validateWithdraw_InvalidSenderId() {
        final var validator = new TransactionControllerValidator();

        validator.validateWithdraw(new WithdrawRequestBody(
                "lol", new BigDecimal("10.21")));
    }

    @Test(expected = InvalidTransactionAmount.class)
    public void validateWithdraw_InvalidAmountSent() {
        final var validator = new TransactionControllerValidator();

        validator.validateWithdraw(new WithdrawRequestBody(
                UUID.randomUUID().toString(), new BigDecimal("-10.21")));
    }

    @Test
    public void validateDeposit_ValidRequest() {
        final var validator = new TransactionControllerValidator();

        validator.validateDeposit(new DepositRequestBody(
                UUID.randomUUID().toString(),
                new BigDecimal("10.21")));
    }

    @Test(expected = InvalidIdException.class)
    public void validateDeposit_InvalidSenderId() {
        final var validator = new TransactionControllerValidator();

        validator.validateDeposit(new DepositRequestBody(
                "lol", new BigDecimal("10.21")));
    }

    @Test(expected = InvalidTransactionAmount.class)
    public void validateDeposit_InvalidAmountSent() {
        final var validator = new TransactionControllerValidator();

        validator.validateDeposit(new DepositRequestBody(
                UUID.randomUUID().toString(), new BigDecimal("-10.21")));
    }
}