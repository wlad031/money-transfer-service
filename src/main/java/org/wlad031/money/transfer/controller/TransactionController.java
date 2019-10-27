package org.wlad031.money.transfer.controller;

import com.google.inject.Inject;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.*;
import org.eclipse.jetty.server.Response;
import org.wlad031.money.transfer.command.Command;
import org.wlad031.money.transfer.query.Query;
import org.wlad031.money.transfer.converter.AbstractConverter;
import org.wlad031.money.transfer.converter.TransactionControllerConverter;
import org.wlad031.money.transfer.exception.TransactionNotFoundException;
import org.wlad031.money.transfer.model.request.CreateNewTransactionRequestBody;
import org.wlad031.money.transfer.model.request.DepositRequestBody;
import org.wlad031.money.transfer.model.request.WithdrawRequestBody;
import org.wlad031.money.transfer.model.response.ErrorResponse;
import org.wlad031.money.transfer.model.response.GetAccountTransactionsResponse;
import org.wlad031.money.transfer.model.response.GetTransactionDetailsResponse;
import org.wlad031.money.transfer.model.response.IdResponse;
import org.wlad031.money.transfer.validator.TransactionControllerValidator;

import static io.javalin.apibuilder.ApiBuilder.*;

public class TransactionController implements Controller {

    private final Query query;
    private final Command command;

    private final TransactionControllerValidator validator;
    private final TransactionControllerConverter converter;

    @Inject
    public TransactionController(Query query, Command command) {
        this.query = query;
        this.command = command;

        this.validator = new TransactionControllerValidator();
        this.converter = new TransactionControllerConverter();
    }

    @Override
    public void bindRoutes() {
        path("/account/:id/transactions", () -> {
            get(this::getAccountTransactions);
        });
        path("/transaction/:id", () -> {
            get(this::getTransactionDetails);
        });
        path("/transaction", () -> {
            post(this::createTransaction);
        });
        path("/transaction/withdraw", () -> {
            post(this::withdraw);
        });
        path("/transaction/deposit", () -> {
            post(this::deposit);
        });
    }

    @Override
    public void bindExceptionHandlers(Javalin javalin) {
        javalin.exception(TransactionNotFoundException.class, (e, ctx) -> {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(Response.SC_NOT_FOUND);
        });
    }

    @OpenApi(
            path = "/transaction/:id",
            method = HttpMethod.GET,
            summary = "Returns transaction details by it's ID",
            description = "Returns transaction details by it's ID",
            responses = {
                    @OpenApiResponse(status = "200",
                            content = @OpenApiContent(from = GetTransactionDetailsResponse.class),
                            description = "Found transaction details"),
                    @OpenApiResponse(status = "400",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Bad request"),
                    @OpenApiResponse(status = "404",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Transaction not found"),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Internal error"),
            }
    )
    public void getTransactionDetails(Context ctx) {
        final var transactionId = ctx.pathParam("id");

        final var transactionDetails = query.getTransactionDetails(
                AbstractConverter.convertId(transactionId))
                .thenApply(converter::convertGetTransactionDetails);

        ctx.json(transactionDetails);
        ctx.status(Response.SC_OK);
    }

    @OpenApi(
            path = "/account/:id/transactions",
            method = HttpMethod.GET,
            summary = "Returns all transactions for the given account",
            description = "Returns all transactions for the given account",
            responses = {
                    @OpenApiResponse(status = "200",
                            content = @OpenApiContent(from = GetAccountTransactionsResponse.class),
                            description = "All found transactions for the given account"),
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
    public void getAccountTransactions(Context ctx) {
        final var accountId = AbstractConverter.convertId(ctx.pathParam("id"));

        final var transactions = query.getAccountTransactions(accountId)
                .thenApply(t -> converter.convertGetAccountTransactionsResponse(accountId, t));

        ctx.json(transactions);
        ctx.status(Response.SC_OK);
    }

    @OpenApi(
            path = "/transaction",
            method = HttpMethod.POST,
            summary = "Creates new transaction",
            description = "Creates new transaction",
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = CreateNewTransactionRequestBody.class),
                    required = true),
            responses = {
                    @OpenApiResponse(status = "201",
                            content = @OpenApiContent(from = IdResponse.class),
                            description = "ID of the created transaction"),
                    @OpenApiResponse(status = "400",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Bad request"),
                    @OpenApiResponse(status = "404",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Sender or receiver account not found"),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Internal error"),
            }
    )
    public void createTransaction(Context ctx) {
        final var body = ctx.bodyAsClass(CreateNewTransactionRequestBody.class);
        validator.validateCreateTransaction(body);

        final var transactionId = query.generateTransactionId();

        command.createNewTransaction(transactionId,
                AbstractConverter.convertId(body.getSenderId()),
                AbstractConverter.convertId(body.getReceiverId()),
                body.getAmountSent(), body.getAmountReceived(),
                body.getDateTime());

        ctx.json(converter.convertIdResponse(transactionId));
        ctx.status(Response.SC_CREATED);
    }

    @OpenApi(
            path = "/transaction/withdraw",
            method = HttpMethod.POST,
            summary = "Withdraws from the given account",
            description = "Withdraws from the given account",
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = WithdrawRequestBody.class),
                    required = true),
            responses = {
                    @OpenApiResponse(status = "201",
                            content = @OpenApiContent(from = IdResponse.class),
                            description = "ID of the created transaction"),
                    @OpenApiResponse(status = "400",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Bad request"),
                    @OpenApiResponse(status = "404",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Sender account not found"),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Internal error"),
            }
    )
    public void withdraw(Context ctx) {
        final var body = ctx.bodyAsClass(WithdrawRequestBody.class);
        validator.validateWithdraw(body);

        final var transactionId = query.generateTransactionId();

        command.withdraw(transactionId,
                AbstractConverter.convertId(body.getAccountId()),
                body.getAmount(), body.getDateTime());

        ctx.json(converter.convertIdResponse(transactionId));
        ctx.status(Response.SC_CREATED);
    }

    @OpenApi(
            path = "/transaction/deposit",
            method = HttpMethod.POST,
            summary = "Deposit to the given account",
            description = "Deposit to the given account",
            requestBody = @OpenApiRequestBody(
                    content = @OpenApiContent(from = DepositRequestBody.class),
                    required = true),
            responses = {
                    @OpenApiResponse(status = "201",
                            content = @OpenApiContent(from = IdResponse.class),
                            description = "ID of the created transaction"),
                    @OpenApiResponse(status = "400",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Bad request"),
                    @OpenApiResponse(status = "404",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Receiver account not found"),
                    @OpenApiResponse(status = "500",
                            content = @OpenApiContent(from = ErrorResponse.class),
                            description = "Internal error"),
            }
    )
    public void deposit(Context ctx) {
        final var body = ctx.bodyAsClass(DepositRequestBody.class);
        validator.validateDeposit(body);

        final var transactionId = query.generateTransactionId();

        command.deposit(transactionId,
                AbstractConverter.convertId(body.getAccountId()),
                body.getAmount(), body.getDateTime());

        ctx.json(converter.convertIdResponse(transactionId));
        ctx.status(Response.SC_CREATED);
    }
}
