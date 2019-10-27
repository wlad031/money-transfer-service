package org.wlad031.money.transfer.config;

import com.google.gson.GsonBuilder;
import com.google.inject.Guice;
import io.javalin.plugin.json.JavalinJson;
import io.javalin.plugin.openapi.jackson.JacksonToJsonMapper;

public class Main {

    public static void main(String[] args) {
        final var injector = Guice.createInjector(new AppModule());
        injector.getInstance(Server.class).boot();
    }
}
