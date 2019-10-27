package org.wlad031.money.transfer.converter;

import io.javalin.http.Context;
import lombok.NonNull;
import org.wlad031.money.transfer.controller.AccountController;
import org.wlad031.money.transfer.model.*;
import org.wlad031.money.transfer.model.response.GetAccountDetailsResponse;
import org.wlad031.money.transfer.model.response.GetAllAccountsResponse;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Converter for {@link AccountController}'s methods
 */
public class AccountControllerConverter extends AbstractConverter {

    /**
     * Converts {@link Account} to the response of
     * {@link AccountController#getAccountDetailsById(Context)}
     *
     * @param account account to convert
     */
    public GetAccountDetailsResponse convertGetAccountDetails(@NonNull Account account) {
        return new GetAccountDetailsResponse(
                convertId(account.getId()),
                account.getName(),
                convertCurrency(account.getCurrency()),
                account.getBalance(),
                convertAccountStatus(account.getStatus()),
                convertDateTime(account.getLastUpdated()));
    }

    /**
     * Converts collection of account IDs to the response of
     * {@link AccountController#getAllAvailableAccountIds(Context)}
     *
     * @param ids collection of IDs to convert
     */
    public GetAllAccountsResponse convertGetAccountIdsResponse(@NonNull Collection<UUID> ids) {
        return new GetAllAccountsResponse(ids.stream()
                .map(UUID::toString)
                .collect(Collectors.toSet()));
    }

    /**
     * Converts value of enum {@link Account.Status} to the value of
     * enum {@link GetAccountDetailsResponse.Status}
     *
     * @param status value to convert
     * @return converted value
     */
    public GetAccountDetailsResponse.Status convertAccountStatus(@NonNull Account.Status status) {
        return convertEnum(status, GetAccountDetailsResponse.Status.class);
    }
}
