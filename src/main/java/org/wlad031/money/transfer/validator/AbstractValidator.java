package org.wlad031.money.transfer.validator;

import lombok.NonNull;
import org.wlad031.money.transfer.exception.InvalidIdException;

import java.util.UUID;

/**
 * This class contains general validation methods
 */
public abstract class AbstractValidator {

    /**
     * Checks that ID is not null and could be parsed as {@link UUID}
     *
     * @param fieldName the fieldName for exception message
     * @param id        ID to check
     */
    public static void validateNotNullableId(@NonNull String fieldName, String id) {
        if (id == null) throw new InvalidIdException(fieldName, null);
        try {
            UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidIdException(fieldName, id);
        }
    }
}
