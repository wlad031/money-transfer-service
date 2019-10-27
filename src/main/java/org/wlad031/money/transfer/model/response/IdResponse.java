package org.wlad031.money.transfer.model.response;

import lombok.NonNull;
import lombok.Value;

@Value
public class IdResponse {
    @NonNull private final String id;
}
