package org.wlad031.money.transfer.converter;

import org.junit.Test;
import org.wlad031.money.transfer.model.Transaction;
import org.wlad031.money.transfer.model.TransactionAmount;
import org.wlad031.money.transfer.model.response.GetAccountTransactionsResponse;
import org.wlad031.money.transfer.model.response.GetTransactionDetailsResponse;
import org.wlad031.money.transfer.model.response.IdResponse;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Currency;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class TransactionControllerConverterTest {

    @Test
    public void convertIdResponse_ValidResponse() {
        final var converter = new TransactionControllerConverter();
        final var id = UUID.randomUUID();

        final var actual = converter.convertIdResponse(id);

        assertEquals(new IdResponse(id.toString()), actual);
    }

    @Test
    public void convertGetAccountTransactionsResponse_AccountIsSender() {
        final var converter = new TransactionControllerConverter();

        final var now = ZonedDateTime.now();
        final var accountId1 = UUID.randomUUID();
        final var accountId2 = UUID.randomUUID();

        final var transactionIds = IntStream.range(0, 5).mapToObj(__ -> UUID.randomUUID()).collect(Collectors.toSet());

        final var transactions = transactionIds.stream()
                .map(id -> new Transaction(id, accountId1, accountId2,
                        new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.12")),
                        new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("10.12")),
                        now))
                .collect(Collectors.toSet());
        final var actual = converter.convertGetAccountTransactionsResponse(accountId1, transactions);

        final var expected = new GetAccountTransactionsResponse(accountId1.toString(), transactions.stream()
                .map(t -> new GetAccountTransactionsResponse.Transaction(t.getId().toString(),
                        GetAccountTransactionsResponse.Transaction.Status.PENDING,
                        accountId1.toString(), accountId2.toString(),
                        GetAccountTransactionsResponse.Transaction.Direction.SENDER, new BigDecimal("10.12"), "EUR",
                        now.toString()))
                .collect(Collectors.toSet()));
        assertEquals(expected, actual);
    }

    @Test
    public void convertGetAccountTransactionsResponse_AccountIsReceiver() {
        final var converter = new TransactionControllerConverter();

        final var now = ZonedDateTime.now();
        final var accountId1 = UUID.randomUUID();
        final var accountId2 = UUID.randomUUID();

        final var transactionIds = IntStream.range(0, 5).mapToObj(__ -> UUID.randomUUID()).collect(Collectors.toSet());

        final var transactions = transactionIds.stream()
                .map(id -> new Transaction(id, accountId2, accountId1,
                        new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.12")),
                        new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("10.12")),
                        now))
                .collect(Collectors.toSet());
        final var actual = converter.convertGetAccountTransactionsResponse(accountId1, transactions);

        final var expected = new GetAccountTransactionsResponse(accountId1.toString(), transactions.stream()
                .map(t -> new GetAccountTransactionsResponse.Transaction(t.getId().toString(),
                        GetAccountTransactionsResponse.Transaction.Status.PENDING,
                        accountId2.toString(), accountId1.toString(),
                        GetAccountTransactionsResponse.Transaction.Direction.RECEIVER, new BigDecimal("10.12"), "RUB",
                        now.toString()))
                .collect(Collectors.toSet()));
        assertEquals(expected, actual);
    }

    @Test(expected = IllegalArgumentException.class)
    public void convertGetAccountTransactionsResponse_AccountDoestHaveTransaction() {
        final var converter = new TransactionControllerConverter();

        final var now = ZonedDateTime.now();
        final var accountId1 = UUID.randomUUID();

        final var transactionIds = IntStream.range(0, 5).mapToObj(__ -> UUID.randomUUID()).collect(Collectors.toSet());

        final var transactions = transactionIds.stream()
                .map(id -> new Transaction(id, UUID.randomUUID(), UUID.randomUUID(),
                        new TransactionAmount(Currency.getInstance("EUR"), new BigDecimal("10.12")),
                        new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("10.12")),
                        now))
                .collect(Collectors.toSet());
        final var actual = converter.convertGetAccountTransactionsResponse(accountId1, transactions);
    }

    @Test
    public void convertGetTransactionDetails_ValidResponse() {
        final var converter = new TransactionControllerConverter();

        final var transactionId = UUID.randomUUID();
        final var senderId = UUID.randomUUID();
        final var receiverId = UUID.randomUUID();
        final var now = ZonedDateTime.now();

        final var actual = converter.convertGetTransactionDetails(new Transaction(transactionId, senderId, receiverId,
                new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("10.21")),
                new TransactionAmount(Currency.getInstance("RUB"), new BigDecimal("10.01")), now));

        final var expected = new GetTransactionDetailsResponse(transactionId.toString(), senderId.toString(), receiverId.toString(),
                GetTransactionDetailsResponse.Status.PENDING,
                new GetTransactionDetailsResponse.TransactionAmount("RUB", new BigDecimal("10.21")),
                new GetTransactionDetailsResponse.TransactionAmount("RUB", new BigDecimal("10.01")),
                now.toString());
        assertEquals(expected, actual);
    }

    @Test
    public void transactionStatusesAreConvertible() {
        final var converter = new TransactionControllerConverter();

        Arrays.stream(Transaction.Status.values()).map(converter::convertTransactionStatus).collect(Collectors.toSet());
    }

    @Test
    public void accountTransactionStatusesAreConvertible() {
        final var converter = new TransactionControllerConverter();

        Arrays.stream(Transaction.Status.values()).map(converter::convertAccountTransactionStatus).collect(Collectors.toSet());
    }
}