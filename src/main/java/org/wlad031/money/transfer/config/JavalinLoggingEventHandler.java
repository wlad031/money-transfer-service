package org.wlad031.money.transfer.config;

import io.javalin.core.event.HandlerMetaInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class JavalinLoggingEventHandler implements Consumer<HandlerMetaInfo> {
    private final Logger logger = LoggerFactory.getLogger("LoggingEventHandler");

    @Override
    public void accept(HandlerMetaInfo handlerMetaInfo) {
        logger.info("Handler added: {}}", handlerMetaInfo);
    }
}
