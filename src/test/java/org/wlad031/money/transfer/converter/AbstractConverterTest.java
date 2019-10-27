package org.wlad031.money.transfer.converter;

import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Currency;
import java.util.UUID;

import static org.junit.Assert.*;

public class AbstractConverterTest {

    @Test
    public void convertDateTime() {
        final var s = "2019-10-27T09:11:19.807444+01:00[Europe/Warsaw]";
        final var dateTime = ZonedDateTime.parse(s);
        assertEquals(s, AbstractConverter.convertDateTime(dateTime));
    }

    @Test
    public void convertCurrency() {
        final var s = "RUB";
        final var currency = Currency.getInstance(s);
        assertEquals(s, AbstractConverter.convertCurrency(currency));
    }

    @Test
    public void convertId_FromUUIDtoString() {
        final var s = "7a3b4569-8bac-4f69-a8ac-523e959aa873";
        final var id = UUID.fromString(s);
        assertEquals(s, AbstractConverter.convertId(id));
    }

    @Test
    public void convertId_FromStringYoUUID() {
        final var s = "7a3b4569-8bac-4f69-a8ac-523e959aa873";
        final var id = UUID.fromString(s);
        assertEquals(id, AbstractConverter.convertId(s));
    }

    @Test
    public void convertEnum() {
        assertEquals(Enum2.VALUE1, AbstractConverter.convertEnum(Enum1.VALUE1, Enum2.class));
        assertEquals(Enum2.VALUE2, AbstractConverter.convertEnum(Enum1.VALUE2, Enum2.class));
        assertEquals(Enum1.VALUE1, AbstractConverter.convertEnum(Enum2.VALUE1, Enum1.class));
        assertEquals(Enum1.VALUE2, AbstractConverter.convertEnum(Enum2.VALUE2, Enum1.class));

        try {
            AbstractConverter.convertEnum(Enum1.VALUE3, Enum2.class);
            fail("Value should not be convertible");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        } catch (Throwable e) {
            fail("Unknown exception");
        }
        try {
            AbstractConverter.convertEnum(Enum2.VALUE4, Enum1.class);
            fail("Value should not be convertible");
        } catch (IllegalArgumentException e) {
            assertNotNull(e);
        } catch (Throwable e) {
            fail("Unknown exception");
        }
    }

    enum Enum1 {
        VALUE1, VALUE2, VALUE3
    }

    enum Enum2 {
        VALUE1, VALUE2, VALUE4
    }
}
