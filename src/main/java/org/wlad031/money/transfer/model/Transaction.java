package org.wlad031.money.transfer.model;

import lombok.Data;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
public class Transaction {

    public enum Status {
        PENDING,
        COMPLETED,
        ABORTED
    }

    @NonNull private final UUID id;
    private final UUID senderId;
    private final UUID receiverId;
    private final TransactionAmount amountSent;
    private final TransactionAmount amountReceived;
    @NonNull private final ZonedDateTime createdDateTime;
    private ZonedDateTime processedDateTime;
    @NonNull private Status status;

    public Transaction(
            @NonNull UUID id,
            UUID senderId, UUID receiverId,
            TransactionAmount amountSent, TransactionAmount amountReceived,
            @NonNull ZonedDateTime createdDateTime) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.amountSent = amountSent;
        this.amountReceived = amountReceived;
        this.createdDateTime = createdDateTime;
        this.status = Status.PENDING;
    }

    public static Transaction deposit(
            @NonNull UUID id,
            @NonNull UUID receiverId, @NonNull TransactionAmount amountReceived,
            @NonNull ZonedDateTime createdDateTime) {
        return new Transaction(id, null, receiverId, null, amountReceived,
                createdDateTime);
    }

    public static Transaction withdraw(
            @NonNull UUID id,
            @NonNull UUID senderId, @NonNull TransactionAmount amountSent,
            @NonNull ZonedDateTime createdDateTime) {
        return new Transaction(id, senderId, null, amountSent, null,
                createdDateTime);
    }

    public synchronized void complete() {
        this.status = Status.COMPLETED;
        this.processedDateTime = ZonedDateTime.now();
    }

    public synchronized void abort() {
        this.status = Status.ABORTED;
        this.processedDateTime = ZonedDateTime.now();
    }
}
