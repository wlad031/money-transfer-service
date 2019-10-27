package org.wlad031.money.transfer.model.request;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Value
@AllArgsConstructor
public class DepositRequestBody {
    @NonNull private final String accountId;
    @NonNull private final BigDecimal amount;
    private final ZonedDateTime dateTime;

    public DepositRequestBody(@NonNull String accountId, @NonNull BigDecimal amount) {
        this(accountId, amount, null);
    }
}
