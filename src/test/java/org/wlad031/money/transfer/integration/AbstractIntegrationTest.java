package org.wlad031.money.transfer.integration;

import io.javalin.Javalin;
import org.wlad031.money.transfer.AbstractTest;

public class AbstractIntegrationTest extends AbstractTest {

    protected static Javalin createJavalin() {
        return Javalin
                .create(config -> {
                    config.showJavalinBanner = false;
                    config.defaultContentType = "application/json";
                });
    }
}
