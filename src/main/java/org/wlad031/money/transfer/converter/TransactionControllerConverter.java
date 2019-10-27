package org.wlad031.money.transfer.converter;

import lombok.NonNull;
import org.wlad031.money.transfer.model.Transaction;
import org.wlad031.money.transfer.model.TransactionAmount;
import org.wlad031.money.transfer.model.response.GetAccountTransactionsResponse;
import org.wlad031.money.transfer.model.response.GetTransactionDetailsResponse;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class TransactionControllerConverter extends AbstractConverter {

    public GetAccountTransactionsResponse convertGetAccountTransactionsResponse(
            @NonNull UUID accountId, @NonNull Collection<Transaction> transactions) {
        return new GetAccountTransactionsResponse(convertId(accountId), transactions.stream()
                .map(t -> convertTransaction(accountId, t))
                .collect(Collectors.toSet()));
    }

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

    private GetTransactionDetailsResponse.TransactionAmount convertTransactionAmount(
            @NonNull TransactionAmount transactionAmount) {
        return new GetTransactionDetailsResponse.TransactionAmount(
                convertCurrency(transactionAmount.getCurrency()),
                transactionAmount.getAmount());
    }

    public GetTransactionDetailsResponse.Status convertTransactionStatus(Transaction.Status status) {
        return convertEnum(status, GetTransactionDetailsResponse.Status.class);
    }

    public GetAccountTransactionsResponse.Transaction.Status convertAccountTransactionStatus(Transaction.Status status) {
        return convertEnum(status, GetAccountTransactionsResponse.Transaction.Status.class);
    }
}
