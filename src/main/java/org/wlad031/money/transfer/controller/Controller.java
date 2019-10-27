package org.wlad031.money.transfer.controller;

import io.javalin.Javalin;

public interface Controller {

    void bindRoutes();

    default void bindExceptionHandlers(Javalin javalin) {}
}
