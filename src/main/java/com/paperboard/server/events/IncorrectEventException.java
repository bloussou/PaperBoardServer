package com.paperboard.server.events;

public class IncorrectEventException extends IllegalArgumentException {

    public IncorrectEventException() {
        super("Incorrect arguments for a new WebSocketMessage !");
    }

    public IncorrectEventException(final Throwable err) {
        super("Incorrect arguments for a new WebSocketMessage !", err);
    }

    public IncorrectEventException(final String errorMsg) {
        super("Incorrect arguments for a new WebSocketMessage !" + errorMsg);
    }

    public IncorrectEventException(final String errorMsg, final Throwable err) {
        super("Incorrect arguments for a new WebSocketMessage !" + errorMsg, err);
    }
}
