package org.wlad031.money.transfer.config;

import com.google.inject.Guice;

public class Main {

    public static void main(String[] args) {
        final var injector = Guice.createInjector(new AppModule());
        injector.getInstance(Server.class).boot();
    }
}
