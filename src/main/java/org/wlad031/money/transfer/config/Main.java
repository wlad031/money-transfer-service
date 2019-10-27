package org.wlad031.money.transfer.config;

import com.google.inject.Guice;
import io.javalin.Javalin;

public class Main {

    public static void main(String[] args) {
        final var injector = Guice.createInjector(new AppModule());
        final var javalin = injector.getInstance(Javalin.class);
        injector.getInstance(Router.class).bindRoutes(javalin);

        javalin.start(8080);
    }
}
