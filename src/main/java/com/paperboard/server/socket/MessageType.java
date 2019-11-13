package com.paperboard.server.socket;

public enum MessageType {

    // ---- RECEIVED FROM CLIENTS
    MSG_JOIN_BOARD("Join Board"),
    MSG_LEAVE_BOARD("Leave Board"),
    MSG_CREATE_OBJECT("Create Object"),
    MSG_EDIT_OBJECT("Edit Object"),
    MSG_LOCK_OBJECT("Lock Object"),
    MSG_UNLOCK_OBJECT("Unlock Object"),

    // ---- SYMMETRIC MESSAGES
    MSG_DELETE_OBJECT("Delete Object"),
    MSG_CHAT_MESSAGE("Chat Message"),

    // ---- SENT TO CLIENTS
    MSG_OBJECT_CREATED("Object Created"),
    MSG_OBJECT_EDITED("Object Edited"),
    MSG_OBJECT_DELETED("Object Deleted"),
    MSG_OBJECT_LOCKED("Object Locked"),
    MSG_OBJECT_UNLOCKED("Object Unlocked"),
    MSG_DRAWER_CONNECTED("New Drawer Connected"),
    MSG_DRAWER_DISCONNECTED("Drawer Disconnected");

    public final String str;

    MessageType(final String str) {
        this.str = str;
    }

    public static boolean contains(final String test) {
        for (final MessageType msgType : MessageType.values()) {
            if (msgType.str.equals(test)) {
                return true;
            }
        }
        return false;
    }

    public static MessageType getEnum(final String str) {
        for (final MessageType msgType : MessageType.values()) {
            if (msgType.str.equals(str)) {
                return msgType;
            }
        }
        return null;
    }
}
