package org.wlad031.money.transfer.model.response;

import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class GetAccountDetailsResponse {

    public enum Status {
        ACTIVE,
        CLOSED
    }

    @NonNull private final String id;
    @NonNull private final String name;
    @NonNull private final String currency;
    @NonNull private final BigDecimal balance;
    @NonNull private final Status status;
    @NonNull private final String lastUpdated;
}
