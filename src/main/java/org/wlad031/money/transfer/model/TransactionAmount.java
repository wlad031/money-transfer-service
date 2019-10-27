package org.wlad031.money.transfer.model;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Data
public class TransactionAmount {
    @NonNull private final Currency currency;
    @NonNull private final BigDecimal amount;
}
