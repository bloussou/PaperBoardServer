package com.paperboard.server.socket;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * MEthod to encode Message before sending
 */
public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public String encode(final Message message) {
        final String encodedMsg = message.toString();
        return encodedMsg;
    }

    @Override
    public void init(final EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }
}
