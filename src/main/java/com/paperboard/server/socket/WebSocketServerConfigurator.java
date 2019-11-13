package com.paperboard.server.socket;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import java.util.Arrays;
import java.util.Collections;

public class WebSocketServerConfigurator extends ServerEndpointConfig.Configurator {

    private static final String[] ALLOWED_METHODS = {"GET", "HEAD", "PUT", "PATCH", "POST", "DELETE"};
    private static final String[] ALLOWED_ORIGINS = {
            "http://localhost:3000",
            "chrome-extension://fgponpodhbmadfljofbimhhlengambbn",
    };

    @Override
    public boolean checkOrigin(final String originHeaderValue) {
        boolean allowed = false;
        for (int i = 0; i < ALLOWED_ORIGINS.length; i++) {
            if (originHeaderValue.equals(ALLOWED_ORIGINS[i])) {
                allowed = true;
                break;
            }
        }
        return allowed;
    }

    @Override
    public void modifyHandshake(final ServerEndpointConfig sec, final HandshakeRequest request, final HandshakeResponse response) {
        response.getHeaders().put("Access-Control-Allow-Headers", Collections.singletonList("content-type"));
        response.getHeaders().put("Access-Control-Allow-Methods", Arrays.asList(ALLOWED_METHODS));
        response.getHeaders().put("Access-Control-Allow-Origin", Arrays.asList(ALLOWED_ORIGINS));
    }

}