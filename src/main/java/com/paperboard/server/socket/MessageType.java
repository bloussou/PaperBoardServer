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
    CHAT_MESSAGE("Chat message"),
    ASK_DELETION("Ask to Delete"),

    // ---- SENT TO CLIENTS
    OBJECT_CREATED("Object Created"),
    OBJECT_EDITED("Object Edited"),
    OBJECT_DELETED("Object Deleted"),
    DRAWER_CONNECTED("New Drawer Connected"),
    DRAWER_DISCONNECTED("Drawer Disconnected");

    private final String type;

    MessageType(final String type) {
        this.type = type;
    }

    public static boolean contains(final String test) {
        for (final MessageType msgType : MessageType.values()) {
            if (msgType.type.equals(test)) {
                return true;
            }
        }
        return false;
    }

}
