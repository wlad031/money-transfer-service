package org.wlad031.money.transfer.controller;

import io.javalin.Javalin;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class ControllerTest {

    @Test
    public void bindExceptionHandlers() {
        final var javalin = mock(Javalin.class);
        final Controller controller = () -> {};
        controller.bindExceptionHandlers(javalin);
        verifyZeroInteractions(javalin);
    }
}