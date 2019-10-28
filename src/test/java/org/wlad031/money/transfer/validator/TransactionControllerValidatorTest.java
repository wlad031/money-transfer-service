package org.wlad031.money.transfer.validator;

import org.junit.Test;
import org.wlad031.money.transfer.exception.InvalidIdException;
import org.wlad031.money.transfer.exception.InvalidTransactionAmount;
import org.wlad031.money.transfer.exception.ValidationException;
import org.wlad031.money.transfer.model.request.CreateNewTransactionRequestBody;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.UUID;

public class TransactionControllerValidatorTest {

    private final TransactionControllerValidator validator = new TransactionControllerValidator();

    @Test(expected = NullPointerException.class)
    public void validateCreateTransaction_NullBody() {
        validator.validateCreateTransaction(null);
    }

    @Test
    public void validateCreateTransaction_ValidRequest() {
        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new BigDecimal("10.21"), new BigDecimal("10.01")));
    }

    @Test(expected = InvalidIdException.class)
    public void validateCreateTransaction_InvalidSenderId() {
        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                "lol", UUID.randomUUID().toString(),
                new BigDecimal("10.21"), new BigDecimal("10.01")));
    }

    @Test(expected = InvalidIdException.class)
    public void validateCreateTransaction_InvalidReceiverId() {
        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), "lol",
                new BigDecimal("10.21"), new BigDecimal("10.01")));
    }

    @Test(expected = InvalidTransactionAmount.class)
    public void validateCreateTransaction_InvalidAmountSent() {
        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new BigDecimal("-10.21"), new BigDecimal("10.01")));
    }

    @Test(expected = InvalidTransactionAmount.class)
    public void validateCreateTransaction_InvalidAmountReceived() {
        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new BigDecimal("10.21"), new BigDecimal("-10.01")));
    }

    @Test(expected = ValidationException.class)
    public void validateCreateTransaction_SenderIsNullAndAmountSentIsNot() {
        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                null, UUID.randomUUID().toString(),
                new BigDecimal("10.0"), new BigDecimal("10.0")
        ));
    }

    @Test(expected = ValidationException.class)
    public void validateCreateTransaction_SenderIsNotNullAndAmountSentIsNull() {
        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                null, new BigDecimal("10.0")
        ));
    }

    @Test(expected = ValidationException.class)
    public void validateCreateTransaction_ReceiverIsNullAndAmountReceiveIsNot() {
        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), null,
                new BigDecimal("10.0"), new BigDecimal("10.0")
        ));
    }

    @Test(expected = ValidationException.class)
    public void validateCreateTransaction_ReceiverIsNotNullAndAmountReceiveIsNull() {
        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(),
                new BigDecimal("10.0"), null
        ));
    }

    @Test(expected = ValidationException.class)
    public void validateCreateTransaction_SenderAndReceiverAreNull() {
        validator.validateCreateTransaction(new CreateNewTransactionRequestBody(
                null, null,
                null, null
        ));
    }
}