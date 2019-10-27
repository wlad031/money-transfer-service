package org.wlad031.money.transfer.model.response;

import lombok.NonNull;
import lombok.Value;

import java.util.Collection;

@Value
public class GetAllAccountsResponse {
    @NonNull private final Collection<String> accountIds;
}
