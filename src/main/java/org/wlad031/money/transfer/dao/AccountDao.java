package org.wlad031.money.transfer.dao;

import lombok.NonNull;
import org.wlad031.money.transfer.model.Account;
import org.wlad031.money.transfer.model.Transaction;

import java.util.Collection;
import java.util.UUID;

public interface AccountDao {

    Account getById(@NonNull UUID id);
    @NonNull Collection<Account> getAll();

    void create(@NonNull Account account);

    void updateAccounts(@NonNull Transaction transaction);
}
