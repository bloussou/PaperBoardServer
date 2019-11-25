package com.paperboard.server.socket;

public class IncorrectMessageException extends IllegalArgumentException {

    public IncorrectMessageException() {
        super("Incorrect arguments for a new WebSocketMessage !");
    }

    public IncorrectMessageException(final Throwable err) {
        super("Incorrect arguments for a new WebSocketMessage !", err);
    }

    public IncorrectMessageException(final String errorMsg) {
        super("Incorrect arguments for a new WebSocketMessage !" + errorMsg);
    }

    public IncorrectMessageException(final String errorMsg, final Throwable err) {
        super("Incorrect arguments for a new WebSocketMessage !" + errorMsg, err);
    }
}
