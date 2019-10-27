package org.wlad031.money.transfer.exception;

import lombok.NonNull;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    private final UUID id;

    public AccountNotFoundException(@NonNull UUID id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Account ID " + id.toString() + " not found";
    }
}
