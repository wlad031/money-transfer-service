package org.wlad031.money.transfer.controller;

import com.google.inject.Inject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import org.eclipse.jetty.server.Response;
import org.wlad031.money.transfer.command.Command;
import org.wlad031.money.transfer.exception.InvalidAccountNameException;
import org.wlad031.money.transfer.query.Query;
import org.wlad031.money.transfer.converter.AccountControllerConverter;
import org.wlad031.money.transfer.exception.AccountNotFoundException;
import org.wlad031.money.transfer.model.request.CreateNewAccountRequestBody;
import org.wlad031.money.transfer.model.response.ErrorResponse;
import org.wlad031.money.transfer.model.response.GetAccountDetailsResponse;
import org.wlad031.money.transfer.model.response.GetAllAccountsResponse;
import org.wlad031.money.transfer.model.response.IdResponse;
import org.wlad031.money.transfer.validator.AccountControllerValidator;

import java.util.Currency;
import java.util.UUID;

import static io.javalin.apibuilder.ApiBuilder.*;

/**
 * The controller for account-related operations
 */
public class AccountController implements Controller {

    private final Query query;
    private final Command command;

    private final AccountControllerValidator validator;
    private final AccountControllerConverter converter;

    @Inject
    public AccountController(Query query, Command command) {
        this.query = query;
        this.command = command;

        this.validator = new AccountControllerValidator();
        this.converter = new AccountControllerConverter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindRoutes() {
        path("/account/:id", () -> {
            get(this::getAccountDetailsById);
        });
        path("/account", () -> {
            get(this::getAllAvailableAccountIds);
            post(this::createNewAccount);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void bindExceptionHandlers(Javalin javalin) {
        javalin.exception(AccountNotFoundException.class, (e, ctx) -> {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(Response.SC_NOT_FOUND);
        });
    }

    @OpenApi(
            path = "/account/:id",
            method = HttpMethod.GET,
            summary = "Returns account details by it's ID",
            description = "Returns account details by it's ID",
            responses = {
                    @OpenApiResponse(status = "200",
                            content = @OpenApiContent(from = GetAccountDetailsResponse.class),
                            description = "Found account details"),
                    @OpenApiResponse(status = "400",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Bad request"),
                    @OpenApiResponse(status = "404",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Account not found"),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Internal error"),
            }
    )
    public void getAccountDetailsById(Context ctx) {
        final var accountId = ctx.pathParam("id");
        validator.validateNotNullableId("id", accountId);
        final var accountDetails = query.getAccountDetailsById(UUID.fromString(accountId))
                .thenApply(converter::convertGetAccountDetails);

        ctx.json(accountDetails);
        ctx.status(Response.SC_OK);
    }

    @OpenApi(
            path = "/account",
            method = HttpMethod.GET,
            summary = "Returns all registered account IDs",
            description = "Returns all registered account IDs",
            responses = {
                    @OpenApiResponse(status = "200",
                            content = @OpenApiContent(from = GetAllAccountsResponse.class),
                            description = "All found account IDs"),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Internal error"),
            }
    )
    public void getAllAvailableAccountIds(Context ctx) {
        final var accountIds = query.getAvailableAccountIds()
                .thenApply(converter::convertGetAccountIdsResponse);

        ctx.json(accountIds);
        ctx.status(Response.SC_OK);
    }

    @OpenApi(
            path = "/account",
            method = HttpMethod.POST,
            summary = "Creates and registers new account",
            description = "Creates and registers new account",
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = CreateNewAccountRequestBody.class),
                    required = true),
            responses = {
                    @OpenApiResponse(status = "201",
                            content = @OpenApiContent(from = IdResponse.class),
                            description = "Created account ID"),
                    @OpenApiResponse(status = "400",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Bad request"),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Internal error"),
            }
    )
    public void createNewAccount(Context ctx) {
        final var body = ctx.bodyAsClass(CreateNewAccountRequestBody.class);
        validator.validateCreateNewAccount(body);

        final var accountId = query.generateAccountId();
        final var currency = Currency.getInstance(body.getCurrency());

        command.createNewAccount(accountId, body.getName(), currency);

        ctx.json(converter.convertIdResponse(accountId));
        ctx.status(Response.SC_CREATED);
    }
}
