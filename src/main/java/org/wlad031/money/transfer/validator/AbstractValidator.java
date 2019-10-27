package org.wlad031.money.transfer.validator;

import org.wlad031.money.transfer.exception.InvalidIdException;
import org.wlad031.money.transfer.exception.ValidationException;

import java.util.UUID;

public abstract class AbstractValidator {

    public void validateNotNullableId(String fieldName, String id) {
        if (id == null) throw new InvalidIdException(fieldName, id);
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidIdException(fieldName, id);
        }
    }
}
