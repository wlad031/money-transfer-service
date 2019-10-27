package org.wlad031.money.transfer.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import io.javalin.Javalin;

@Singleton
public class Server {

    private final Javalin javalin;
    private final Router router;
    private final int port;

    @Inject
    public Server(Javalin javalin, Router router, @Named("server.port") int port) {
        this.javalin = javalin;
        this.router = router;
        this.port = port;
    }

    public void boot() {
        router.bindRoutes(javalin);
        javalin.start(port);
    }
}
