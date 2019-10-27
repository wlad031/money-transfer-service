package org.wlad031.money.transfer.exception;

import lombok.NonNull;

import java.util.UUID;

public class AccountNotFoundException extends ValidationException {

    public AccountNotFoundException(@NonNull UUID id) {
        super("Account ID " + id.toString() + " not found");
    }
}
