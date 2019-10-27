package org.wlad031.money.transfer.model;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;

@Data
public class Account {

    public enum Status {
        ACTIVE,
        CLOSED
    }

    @NonNull private final UUID id;
    @NonNull private String name;
    @NonNull private final Currency currency;
    @NonNull private volatile BigDecimal balance;
    @NonNull private volatile Status status;
    @NonNull private volatile ZonedDateTime lastUpdated;

    public Account(@NonNull UUID id, @NonNull String name, @NonNull Currency currency) {
        this.id = id;
        this.name = name;
        this.currency = currency;
        this.balance = BigDecimal.ZERO;
        this.status = Status.ACTIVE;
        this.lastUpdated = ZonedDateTime.now();
    }

    public synchronized void add(@NonNull BigDecimal amount) {
        this.balance = this.balance.add(amount);
        this.lastUpdated = ZonedDateTime.now();
    }

    public synchronized void setStatus(@NonNull Status status) {
        this.status = status;
        this.lastUpdated = ZonedDateTime.now();
    }

    public synchronized void setName(@NonNull String name) {
        this.name = name;
        this.lastUpdated = ZonedDateTime.now();
    }
}
