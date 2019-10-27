package org.wlad031.money.transfer.model.response;

import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.Collection;

@Value
public class GetAccountTransactionsResponse {

    @Value
    public static class Transaction {

        public enum Status {
            PENDING,
            COMPLETED,
            ABORTED
        }

        public enum Direction {
            SENDER,
            RECEIVER
        }

        @NonNull private final String id;
        @NonNull private final Status status;
        private final String senderId;
        private final String receiverId;
        @NonNull private final Direction direction;
        @NonNull private final BigDecimal amount;
        @NonNull private final String currency;
        @NonNull private final String dateTime;
    }

    @NonNull private final String id;
    @NonNull private final Collection<Transaction> transactions;
}
