package com.paperboard.server.socket;

import java.io.Serializable;

public class SocketMessage implements Serializable {
    private final String type;
    private final String text;

    public SocketMessage(final String type, final String text) {
        this.type = type;
        this.text = text;
    }

    public String getType() {
        return this.type;
    }

    public String getText() {
        return text;
    }

}
