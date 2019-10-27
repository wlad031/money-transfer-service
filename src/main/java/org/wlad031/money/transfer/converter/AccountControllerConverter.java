package org.wlad031.money.transfer.converter;

import lombok.NonNull;
import org.wlad031.money.transfer.model.*;
import org.wlad031.money.transfer.model.response.GetAccountDetailsResponse;
import org.wlad031.money.transfer.model.response.GetAllAccountsResponse;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountControllerConverter extends AbstractConverter {

    public GetAccountDetailsResponse convertGetAccountDetails(@NonNull Account account) {
        return new GetAccountDetailsResponse(
                convertId(account.getId()),
                account.getName(),
                convertCurrency(account.getCurrency()),
                account.getBalance(),
                convertAccountStatus(account.getStatus()),
                convertDateTime(account.getLastUpdated()));
    }

    public GetAllAccountsResponse convertGetAccountIdsResponse(@NonNull Collection<UUID> ids) {
        return new GetAllAccountsResponse(ids.stream()
                .map(UUID::toString)
                .collect(Collectors.toSet()));
    }

    public GetAccountDetailsResponse.Status convertAccountStatus(@NonNull Account.Status status) {
        return convertEnum(status, GetAccountDetailsResponse.Status.class);
    }
}
