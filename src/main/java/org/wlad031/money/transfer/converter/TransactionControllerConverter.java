package org.wlad031.money.transfer.converter;

import io.javalin.http.Context;
import lombok.NonNull;
import org.wlad031.money.transfer.controller.TransactionController;
import org.wlad031.money.transfer.model.Transaction;
import org.wlad031.money.transfer.model.TransactionAmount;
import org.wlad031.money.transfer.model.response.GetAccountTransactionsResponse;
import org.wlad031.money.transfer.model.response.GetTransactionDetailsResponse;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Converter for {@link TransactionController}'s methods
 */
public class TransactionControllerConverter extends AbstractConverter {

    /**
     * Converts collection of transactions to the response of
     * {@link TransactionController#getAccountTransactions(Context)}
     *
     * @param accountId    ID of account that was used for searching transactions
     * @param transactions transaction collection to convert
     */
    public GetAccountTransactionsResponse convertGetAccountTransactionsResponse(
            @NonNull UUID accountId, @NonNull Collection<Transaction> transactions) {
        return new GetAccountTransactionsResponse(convertId(accountId), transactions.stream()
                .map(t -> convertTransaction(accountId, t))
                .collect(Collectors.toSet()));
    }

    /**
     * Converts one transaction to {@link GetAccountTransactionsResponse.Transaction}
     *
     * @param accountId    ID of account that was used for searching transactions
     * @param transaction  transaction to convert
     */
    private GetAccountTransactionsResponse.Transaction convertTransaction(
            @NonNull UUID accountId, @NonNull Transaction transaction) {
        if (!accountId.equals(transaction.getSenderId()) && !accountId.equals(transaction.getReceiverId())) {
            throw new IllegalArgumentException("Given account id=" + accountId.toString() + " is not sender nor " +
                    "receiver of transaction with id=" + transaction.getId());
        }

        final var direction = accountId.equals(transaction.getSenderId())
                ? GetAccountTransactionsResponse.Transaction.Direction.SENDER
                : GetAccountTransactionsResponse.Transaction.Direction.RECEIVER;
        final var amount = accountId.equals(transaction.getSenderId())
                ? transaction.getAmountSent()
                : transaction.getAmountReceived();
        return new GetAccountTransactionsResponse.Transaction(
                convertId(transaction.getId()),
                convertAccountTransactionStatus(transaction.getStatus()),
                convertId(transaction.getSenderId()), convertId(transaction.getReceiverId()),
                direction,
                amount.getAmount(), convertCurrency(amount.getCurrency()),
                convertDateTime(transaction.getCreatedDateTime()));
    }

    /**
     * Converts transaction to the response of
     * {@link TransactionController#getTransactionDetails(Context)}
     *
     * @param transaction transaction convert
     */
    public GetTransactionDetailsResponse convertGetTransactionDetails(@NonNull Transaction transaction) {
        return new GetTransactionDetailsResponse(
                convertId(transaction.getId()),
                convertId(transaction.getSenderId()),
                convertId(transaction.getReceiverId()),
                convertTransactionStatus(transaction.getStatus()),
                convertTransactionAmount(transaction.getAmountSent()),
                convertTransactionAmount(transaction.getAmountReceived()),
                convertDateTime(transaction.getCreatedDateTime()));
    }

    /**
     * Converts {@link TransactionAmount} to {@link GetTransactionDetailsResponse.TransactionAmount}
     * @param transactionAmount value to convert
     * @return converted value
     */
    private GetTransactionDetailsResponse.TransactionAmount convertTransactionAmount(
            @NonNull TransactionAmount transactionAmount) {
        return new GetTransactionDetailsResponse.TransactionAmount(
                convertCurrency(transactionAmount.getCurrency()),
                transactionAmount.getAmount());
    }

    /**
     * Converts value of enum {@link Transaction.Status} to the value of
     * enum {@link GetTransactionDetailsResponse.Status}
     *
     * @param status value to convert
     * @return converted value
     */
    public GetTransactionDetailsResponse.Status convertTransactionStatus(@NonNull Transaction.Status status) {
        return convertEnum(status, GetTransactionDetailsResponse.Status.class);
    }

    /**
     * Converts value of enum {@link Transaction.Status} to the value of
     * enum {@link GetAccountTransactionsResponse.Transaction.Status}
     *
     * @param status value to convert
     * @return converted value
     */
    public GetAccountTransactionsResponse.Transaction.Status convertAccountTransactionStatus(@NonNull Transaction.Status status) {
        return convertEnum(status, GetAccountTransactionsResponse.Transaction.Status.class);
    }
}
