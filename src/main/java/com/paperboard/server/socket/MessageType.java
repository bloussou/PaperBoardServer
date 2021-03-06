package com.paperboard.server.socket;

/**
 * Enum of the differents message types
 */
public enum MessageType {

    // ---- RECEIVED FROM CLIENTS
    MSG_IDENTIFY("Ask Pseudo"),
    MSG_GET_BOARD("Get Board"),
    MSG_GET_ALL_BOARDS("Get All Boards"),
    MSG_CREATE_BOARD("Create Board"),
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
    MSG_IDENTITY_ANSWER("Pseudo Request Answer"),
    MSG_ANSWER_GET_BOARD("Answer Get Board"),
    MSG_ANSWER_GET_ALL_BOARDS("Answer Get All Boards"),
    MSG_ANSWER_CREATE_BOARD("Answer Create Board"),
    MSG_DRAWER_JOINED_BOARD("New Drawer Joined Board"),
    MSG_DRAWER_LEFT_BOARD("Drawer Left Board"),
    MSG_OBJECT_CREATED("Object Created"),
    MSG_OBJECT_EDITED("Object Edited"),
    MSG_OBJECT_DELETED("Object Deleted"),
    MSG_OBJECT_LOCKED("Object Locked"),
    MSG_OBJECT_UNLOCKED("Object Unlocked");

    public final String str;

    MessageType(final String str) {
        this.str = str;
    }

    /**
     * Check if the value is in the enum
     *
     * @param test the value you want to check
     * @return boolean
     */
    public static boolean contains(final String test) {
        for (final MessageType msgType : MessageType.values()) {
            if (msgType.str.equals(test)) {
                return true;
            }
        }
        return false;
    }

    /**
     * get the modification type of a specified string
     *
     * @param str the key you want to find
     * @return ModificationType
     */
    public static MessageType getEnum(final String str) {
        for (final MessageType msgType : MessageType.values()) {
            if (msgType.str.equals(str)) {
                return msgType;
            }
        }
        return null;
    }
}
