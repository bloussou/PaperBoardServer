package com.paperboard.server.socket;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;
import java.io.StringReader;
import java.util.logging.Logger;

public class MessageDecoder implements Decoder.Text<Message> {

    private static final Logger LOGGER = Logger.getLogger(MessageDecoder.class.getName());

    /**
     * Transform the input string into a Message
     */
    @Override
    public Message decode(final String string) {
        try {
            final JsonObject json = Json.createReader(new StringReader(string)).readObject();
            final String type = json.getString("type");
            final String from = json.getString("from");
            final String to = json.getString("to");
            final JsonObject payload = json.getJsonObject("payload");

            final Message msg = new Message(type, from, to, payload);
            return msg;
        } catch (final IncorrectMessageException e) {
            LOGGER.warning(e.getMessage());
            return null;
        } catch (final Exception e) {
            LOGGER.warning("SocketMessage parsing error : " +
                           string +
                           " is not a valid JSON object (It should not " +
                           "contain other embedding than payload:{}).");
            return null;
        }
    }

    /**
     * Checks whether the input can be turned into a valid Message object
     * in this case, if we can read it as a Json object, we can.
     */
    @Override
    public boolean willDecode(final String string) {
        try {
            Json.createReader(new StringReader(string)).read();
            return true;
        } catch (final Exception ex) {
            LOGGER.warning("SocketMessage cannot be decoded : " +
                           string +
                           " is not a valid JSON object (It should " +
                           "respect JSON format with double quotes and it should not contain other embedding than " +
                           "payload:{}" +
                           ".");
            return false;
        }
    }

    /**
     * The following two methods are placeholders as we don't need to do anything
     * special for init or destroy.
     */
    @Override
    public void init(final EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }
}
