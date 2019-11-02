package com.paperboard.server.socket;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

public class MessageEncoder implements Encoder.Text<Message> {

    @Override
    public String encode(final Message message) throws EncodeException {
        final String encodedMsg = message.toString();
        System.out.println("Encoded msg: " + encodedMsg);
        return encodedMsg;
    }

    @Override
    public void init(final EndpointConfig config) {
        System.out.println("Encoder Init");
    }

    @Override
    public void destroy() {
        System.out.println("Encoder destroy");
    }
}
