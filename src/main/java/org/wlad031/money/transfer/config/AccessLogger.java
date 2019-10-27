package org.wlad031.money.transfer.config;

import io.javalin.http.Context;
import io.javalin.http.RequestLogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccessLogger implements RequestLogger {
    private final Logger logger = LoggerFactory.getLogger("access-log");

    @Override
    public void handle(@NotNull Context ctx, @NotNull Float executionTimeMs) throws Exception {
        logger.info("Got request: path={}, method={}, time={}ms", ctx.path(), ctx.method(),
                executionTimeMs);
    }
}
