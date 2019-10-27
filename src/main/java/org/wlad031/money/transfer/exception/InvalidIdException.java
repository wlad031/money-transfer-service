package org.wlad031.money.transfer.exception;

import lombok.NonNull;

public class InvalidIdException extends ValidationException {

    public InvalidIdException(@NonNull String fieldName, String id) {
        super("Invalid ID=" + id + " for field " + fieldName);
    }
}
