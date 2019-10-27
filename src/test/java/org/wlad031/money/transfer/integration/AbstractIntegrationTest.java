package org.wlad031.money.transfer.integration;

import io.javalin.Javalin;

import static junit.framework.TestCase.fail;

public abstract class AbstractIntegrationTest {

    protected static Javalin createJavalin() {
        return Javalin
                .create(config -> {
                    config.showJavalinBanner = false;
                    config.defaultContentType = "application/json";
                });
    }

    protected static void asyncAssert(int timeoutMs, int nTries, Runnable assertion) {
        try {
            for (int i = 0; i < nTries; i++) {
                try {
                    assertion.run();
                    break;
                } catch (Exception e) {
                    Thread.sleep(timeoutMs);
                }
            }
        } catch (InterruptedException e) {
            fail("Async assertion interrupted");
        }
    }
}
