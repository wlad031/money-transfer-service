package org.wlad031.money.transfer.exception;

import lombok.NonNull;

public class InvalidIdException extends ValidationException {

    public InvalidIdException(@NonNull String fieldName, String id) {
        super(id == null
                ? "Null ID for field " + fieldName
                : "Invalid ID=" + id + " for field " + fieldName);
    }
}
