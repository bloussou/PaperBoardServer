package com.paperboard.server.socket;

/**
 * Exception thrown when json object of a message doesnt contain the good keys
 */
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
