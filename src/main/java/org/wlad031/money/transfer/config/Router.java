package org.wlad031.money.transfer.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import org.eclipse.jetty.server.Response;
import org.wlad031.money.transfer.controller.AccountController;
import org.wlad031.money.transfer.controller.TransactionController;
import org.wlad031.money.transfer.exception.ValidationException;
import org.wlad031.money.transfer.model.response.ErrorResponse;

@Singleton
public class Router {

    private AccountController accountController;
    private TransactionController transactionController;

    @Inject
    public Router(
            AccountController accountController,
            TransactionController transactionController) {
        this.accountController = accountController;
        this.transactionController = transactionController;
    }

    public void bindRoutes(Javalin javalin) {
        javalin.routes(() -> {
            accountController.bindRoutes();
            transactionController.bindRoutes();
        });

        accountController.bindExceptionHandlers(javalin);
        transactionController.bindExceptionHandlers(javalin);

        javalin.exception(ValidationException.class, (e, ctx) -> {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(Response.SC_BAD_REQUEST);
        });
        javalin.exception(BadRequestResponse.class, (e, ctx) -> {
            ctx.json(new ErrorResponse(e.getMessage()));
            ctx.status(Response.SC_BAD_REQUEST);
        });
    }
}