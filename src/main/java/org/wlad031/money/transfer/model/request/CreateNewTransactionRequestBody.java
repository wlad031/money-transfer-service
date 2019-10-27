package org.wlad031.money.transfer.model.request;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Value
@AllArgsConstructor
public class CreateNewTransactionRequestBody {
    @NonNull private final String senderId;
    @NonNull private final String receiverId;
    @NonNull private final BigDecimal amountSent;
    @NonNull private final BigDecimal amountReceived;
    private final ZonedDateTime dateTime;

    public CreateNewTransactionRequestBody(@NonNull String senderId, @NonNull String receiverId, @NonNull BigDecimal amountSent, @NonNull BigDecimal amountReceived) {
        this(senderId, receiverId, amountSent, amountReceived, null);
    }
}
