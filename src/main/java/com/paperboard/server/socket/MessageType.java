package com.paperboard.server.socket;

public enum MessageType {

    // ---- RECEIVED FROM CLIENTS
    JOIN_BOARD("Join Board"),
    LEAVE_BOARD("Leave Board"),
    CREATE_OBJECT("Create Object"),
    EDIT_OBJECT("Edit Object"),
    LOCK_OBJECT("Lock Object"),
    UNLOCK_OBJECT("Unlock Object"),

    // ---- SYMMETRIC MESSAGES
    CHAT_MESSAGE("Chat Message"),
    DELETE_OBJECT("Delete Object"),

    // ---- SENT TO CLIENTS
    OBJECT_CREATED("Object Created"),
    OBJECT_EDITED("Object Edited"),
    OBJECT_DELETED("Object Deleted"),
    OBJECT_LOCKED("Object Locked"),
    OBJECT_UNLOCKED("Object Unlocked"),
    DRAWER_CONNECTED("New Drawer Connected"),
    DRAWER_DISCONNECTED("Drawer Disconnected");

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
