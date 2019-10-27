package org.wlad031.money.transfer.converter;

import org.junit.Test;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.response.GetAccountDetailsResponse;
import org.wlad031.money.transfer.model.response.GetAllAccountsResponse;
import org.wlad031.money.transfer.model.response.IdResponse;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

public class AccountControllerConverterTest {

    @Test
    public void convertIdResponse_ValidResponse() {
        final var converter = new AccountControllerConverter();
        final var id = UUID.randomUUID();

        final var actual = converter.convertIdResponse(id);

        assertEquals(new IdResponse(id.toString()), actual);
    }

    @Test
    public void convertGetAccountDetailsResponse_ValidResponse() {
        final var converter = new AccountControllerConverter();
        final var id = UUID.randomUUID();
        final var account = new Account(id, "name", Currency.getInstance("EUR"));
        account.setBalance(new BigDecimal("10.12"));

        final var actual = converter.convertGetAccountDetails(account);

        final var expected = new GetAccountDetailsResponse(id.toString(), "name", "EUR",
                new BigDecimal("10.12"), GetAccountDetailsResponse.Status.ACTIVE, account.getLastUpdated().toString());
        assertEquals(expected, actual);
    }

    @Test
    public void convertConvertGetAccountIdsResponse_ValidResponse() {
        final var converter = new AccountControllerConverter();
        final var ids = IntStream.range(0, 10).mapToObj(__ -> UUID.randomUUID()).collect(Collectors.toSet());

        final var actual = converter.convertGetAccountIdsResponse(ids);

        final var expected = new GetAllAccountsResponse(ids.stream().map(UUID::toString).collect(Collectors.toSet()));
        assertEquals(expected, actual);
    }

    @Test
    public void accountStatusesAreConvertible() {
        final var converter = new AccountControllerConverter();

        Arrays.stream(Account.Status.values()).map(converter::convertAccountStatus).collect(Collectors.toSet());
    }
}
