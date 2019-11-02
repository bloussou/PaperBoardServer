package com.paperboard.server.socket;

import javax.websocket.server.ServerEndpointConfig;

public class WebSocketServerConfigurator extends ServerEndpointConfig.Configurator {
    private static final String[] ORIGINS = {
            "http://localhost:3000",
            "chrome-extension://fgponpodhbmadfljofbimhhlengambbn",
    };

    @Override
    public boolean checkOrigin(final String originHeaderValue) {
        boolean allowed = false;
        System.out.println("Checking if origin " + originHeaderValue + " is allowed.");
        for (int i = 0; i < ORIGINS.length; i++) {
            if (originHeaderValue.equals(ORIGINS[i])) {
                allowed = true;
                break;
            }
        }
        return allowed;
    }
}