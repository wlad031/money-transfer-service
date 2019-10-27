package org.wlad031.money.transfer.exception;

import lombok.NonNull;

public class InvalidIdException extends ValidationException {

    private final String fieldName;
    private final String id;

    public InvalidIdException(@NonNull String fieldName, String id) {
        this.fieldName = fieldName;
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "Invalid ID=" + id + " for field " + fieldName;
    }
}
