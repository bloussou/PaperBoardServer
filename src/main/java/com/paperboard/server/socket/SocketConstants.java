package com.paperboard.server.socket;

public final class SocketConstants {

    private SocketConstants() {
        // restrict instantiation
    }

    public static final String TYPE_JOIN_BOARD = "Join PaperBoard";
    public static final String TYPE_DRAW = "Draw";
    public static final String TYPE_EDIT = "Edit Drawing";
    public static final String TYPE_DELETE = "Delete Drawing";
    public static final String TYPE_MOVE = "Move Drawing";
    public static final String TYPE_LEAVE_BOARD = "Leave PaperBoard";

}
