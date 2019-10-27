package org.wlad031.money.transfer.config;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import io.javalin.Javalin;
import io.javalin.plugin.openapi.OpenApiOptions;
import io.javalin.plugin.openapi.OpenApiPlugin;
import io.javalin.plugin.openapi.ui.SwaggerOptions;
import io.swagger.v3.oas.models.info.Info;
import org.slf4j.LoggerFactory;
import org.wlad031.money.transfer.command.Command;
import org.wlad031.money.transfer.command.CommandImpl;
import org.wlad031.money.transfer.query.Query;
import org.wlad031.money.transfer.query.QueryImpl;
import org.wlad031.money.transfer.controller.AccountController;
import org.wlad031.money.transfer.controller.TransactionController;
import org.wlad031.money.transfer.dao.*;

import java.io.IOException;
import java.util.Properties;

public class AppModule extends AbstractModule {

    @Override
    protected void configure() {
        try {
            final var props = new Properties();
            final var propertiesResource = getClass().getClassLoader().getResourceAsStream("application.properties");
            props.load(propertiesResource);
            Names.bindProperties(binder(), props);

            bind(Javalin.class).toInstance(createJavalin());

            bind(Router.class);
            bind(Server.class);
            bind(SimpleInMemoryDataSource.class);
            bind(Query.class).to(QueryImpl.class);
            bind(Command.class).to(CommandImpl.class);
            bind(AccountDao.class).to(SimpleInMemoryAccountDao.class);
            bind(TransactionDao.class).to(SimpleInMemoryTransactionDao.class);
            bind(AccountController.class);
            bind(TransactionController.class);
        } catch (IOException e) {
            LoggerFactory.getLogger("AppConfiguration").error("Error happened when trying to run the application", e);
        }
    }

    private Javalin createJavalin() {
        return Javalin
                .create(config -> {
                    config.showJavalinBanner = false;
                    config.defaultContentType = "application/json";
                    config.requestLogger(new AccessLogger());
                    config.registerPlugin(new OpenApiPlugin(getOpenApiOptions()));
                })
                .events(event -> {
                    event.handlerAdded(new JavalinLoggingEventHandler());
                });
    }

    private OpenApiOptions getOpenApiOptions() {
        return new OpenApiOptions(new Info()
                .version("0.0.1")
                .description("Money Transfer Service"))
                .path("/swagger-docs")
                .swagger(new SwaggerOptions("/swagger").title("Money Transfer Service - Swagger Documentation"));
    }
}