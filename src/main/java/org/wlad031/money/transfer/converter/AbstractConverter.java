package org.wlad031.money.transfer.converter;

import lombok.NonNull;
import org.wlad031.money.transfer.model.response.IdResponse;

import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;

/**
 * This class contains general convert methods
 */
public abstract class AbstractConverter {

    /**
     * Converts {@link UUID} to {@link IdResponse}
     *
     * @param id UUID to convert
     * @return converted UUID
     */
    public static IdResponse convertIdResponse(@NonNull UUID id) {
        return new IdResponse(id.toString());
    }

    /**
     * Converts {@link UUID} to {@link String}
     *
     * @param id UUID to convert
     * @return converted UUID
     */
    public static String convertId(@NonNull UUID id) {
        return id.toString();
    }

    /**
     * Converts {@link String} to {@link UUID}
     *
     * @param id ID as string to convert
     * @return converted ID
     */
    public static UUID convertId(@NonNull String id) {
        return UUID.fromString(id);
    }

    /**
     * Converts value of one Enum to appropriate value of another Enum
     *
     * @param value value to be converted
     * @param clazz class of destination Enum
     * @param <T>   the type of source Enum
     * @param <K>   the type of destination Enum
     * @return converted value
     */
    public static <T extends Enum<T>, K extends Enum<K>> K convertEnum(
            @NonNull T value, @NonNull Class<K> clazz) {
        return Enum.valueOf(clazz, value.toString());
    }

    /**
     * Converts {@link ZonedDateTime} to {@link String}
     *
     * @param dateTime value to convert
     * @return converted dateTime
     */
    public static String convertDateTime(@NonNull ZonedDateTime dateTime) {
        return dateTime.toString();
    }

    /**
     * Converts {@link Currency} to {@link String}
     *
     * @param currency value to convert
     * @return converted currency
     */
    public static String convertCurrency(@NonNull Currency currency) {
        return currency.toString();
    }
}
