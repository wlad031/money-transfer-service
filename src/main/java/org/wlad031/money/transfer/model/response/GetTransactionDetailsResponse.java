package org.wlad031.money.transfer.model.response;

import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class GetTransactionDetailsResponse {

    public enum Status {
        PENDING,
        COMPLETED,
        ABORTED
    }

    @Value
    public static class TransactionAmount {
        @NonNull private final String currency;
        @NonNull private final BigDecimal amount;
    }

    @NonNull private final String id;
    private final String senderId;
    private final String receiverId;
    @NonNull private final Status status;
    private final TransactionAmount amountSent;
    private final TransactionAmount amountReceived;
    @NonNull private final String dateTime;
}

