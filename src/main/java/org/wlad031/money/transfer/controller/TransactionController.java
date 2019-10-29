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
import org.wlad031.money.transfer.model.response.ErrorResponse;
import org.wlad031.money.transfer.model.response.GetAccountTransactionsResponse;
import org.wlad031.money.transfer.model.response.GetTransactionDetailsResponse;
import org.wlad031.money.transfer.model.response.IdResponse;
import org.wlad031.money.transfer.validator.AbstractValidator;
import org.wlad031.money.transfer.validator.TransactionControllerValidator;

import java.util.UUID;

import static io.javalin.apibuilder.ApiBuilder.*;
import static org.wlad031.money.transfer.converter.AbstractConverter.convertId;
import static org.wlad031.money.transfer.converter.AbstractConverter.convertIdResponse;
import static org.wlad031.money.transfer.validator.AbstractValidator.*;

/**
 * The controller for transaction-related operations
 */
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

    /**
     * {@inheritDoc}
     */
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
    }

    /**
     * {@inheritDoc}
     */
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
        validateNotNullableId("id", transactionId);

        final var transactionDetails = query.getTransactionDetails(
                convertId(transactionId))
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
        final var accountId = ctx.pathParam("id");
        validateNotNullableId("id", accountId);

        final var id = convertId(accountId);
        final var transactions = query.getAccountTransactions(id)
                .thenApply(t -> converter.convertGetAccountTransactionsResponse(id, t));

        ctx.json(transactions);
        ctx.status(Response.SC_OK);
    }

    @OpenApi(
            path = "/transaction",
            method = HttpMethod.POST,
            summary = "Creates new transaction",
            description = "" +
                    "Creates new transaction\n" +
                    "\n" +
                    "Supports 3 types of transactions:\n" +
                    "   1. normal transactions - just send money from one account to another\n" +
                    "      when sender and receiver IDs are not null\n" +
                    "   2. deposit - add money to some account\n" +
                    "      when sender ID is null\n" +
                    "   3. withdrawal - withdraw money from some account\n" +
                    "      when receiver ID is null",
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

        if (body.getSenderId() == null) {
            command.createDepositTransaction(transactionId, convertId(body.getReceiverId()),
                    body.getAmountReceived(), body.getDateTime());
        } else if (body.getReceiverId() == null) {
            command.createWithdrawalTransaction(transactionId, convertId(body.getSenderId()),
                    body.getAmountSent(), body.getDateTime());
        } else {
            command.createNewTransaction(transactionId,
                    body.getSenderId() == null ? null : convertId(body.getSenderId()),
                    body.getReceiverId() == null ? null : convertId(body.getReceiverId()),
                    body.getAmountSent(),
                    body.getAmountReceived(),
                    body.getDateTime());
        }

        ctx.json(convertIdResponse(transactionId));
        ctx.status(Response.SC_CREATED);
    }
}
