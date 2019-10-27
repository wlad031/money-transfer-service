package org.wlad031.money.transfer.controller;

import io.javalin.Javalin;

/**
 * Basic interface for service's controllers
 */
public interface Controller {

    /**
     * Binds controller's routes
     */
    void bindRoutes();

    /**
     * Binds controller's exception handlers if necessary
     *
     * @param javalin the Javalin instance to bind handlers
     * @see Javalin
     */
    default void bindExceptionHandlers(Javalin javalin) {
    }
}
