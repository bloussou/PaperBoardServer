package com.paperboard.server.events;

/**
 * Custom exceptions for event handling.
 */
public class IncorrectEventException extends IllegalArgumentException {

    public IncorrectEventException(final String errorMsg) {
        super("Incorrect arguments for a new WebSocketMessage !" + errorMsg);
    }
}
