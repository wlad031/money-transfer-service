package org.wlad031.money.transfer.model.response;

import lombok.Value;

@Value
public class ErrorResponse {
    private final String message;
}
