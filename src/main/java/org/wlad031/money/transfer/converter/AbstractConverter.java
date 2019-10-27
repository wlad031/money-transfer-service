package org.wlad031.money.transfer.converter;

import lombok.NonNull;
import org.wlad031.money.transfer.model.response.IdResponse;

import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;

public abstract class AbstractConverter {

    public IdResponse convertIdResponse(@NonNull UUID id) {
        return new IdResponse(id.toString());
    }

    public static String convertId(@NonNull UUID id) {
        return id.toString();
    }

    public static UUID convertId(@NonNull String id) {
        return UUID.fromString(id);
    }

    public static <T extends Enum<T>, K extends Enum<K>> K convertEnum(
            @NonNull T value, @NonNull Class<K> clazz) {
        return Enum.valueOf(clazz, value.toString());
    }

    public static String convertDateTime(@NonNull ZonedDateTime dateTime) {
        return dateTime.toString();
    }

    public static String convertCurrency(@NonNull Currency currency) {
        return currency.toString();
    }
}
