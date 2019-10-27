package org.wlad031.money.transfer.controller;

import io.javalin.http.Context;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.wlad031.money.transfer.command.CommandImpl;
import org.wlad031.money.transfer.dao.*;
import org.wlad031.money.transfer.model.request.CreateNewAccountRequestBody;
import org.wlad031.money.transfer.query.QueryImpl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class AccountControllerTest {

    @Test
    public void test() {
        final var dataSource = new SimpleInMemoryDataSource();
        final var accountController = createController(dataSource);

        final var context = Mockito.mock(Context.class);

        final var captor = ArgumentCaptor.forClass(Object.class);

        Mockito.when(context.bodyAsClass(eq(CreateNewAccountRequestBody.class)))
                .thenReturn(new CreateNewAccountRequestBody("name", "EUR"));

        accountController.createNewAccount(context);

        System.out.println(captor.getValue());
    }

    private AccountController createController(SimpleInMemoryDataSource dataSource) {
        final var accountDao = new SimpleInMemoryAccountDao(dataSource);
        final var transactionDatasource = new SimpleInMemoryTransactionDao(dataSource);
        final var query = new QueryImpl(accountDao, transactionDatasource);
        final var command = new CommandImpl(accountDao, transactionDatasource);
        return new AccountController(query, command);
    }
}
