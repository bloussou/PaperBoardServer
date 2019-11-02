package com.paperboard.server;

import java.util.concurrent.atomic.AtomicLong;


/**
 * User class to define the user object
 */
public class User {
    private static AtomicLong idCounter = new AtomicLong(0);
    private final String id;
    private String pseudo;

    public User(final String pseudo) {
        this.id = "user" + String.valueOf(idCounter.getAndIncrement());
        this.pseudo = pseudo;
    }

    public void joinPaperBoard() {
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(final String pseudo) {
        this.pseudo = pseudo;
    }

    @Override
    public String toString() {
        return this.pseudo;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        final User user = (User) obj;
        return this.getPseudo().equals(user.getPseudo());
    }

    @Override
    public int hashCode() {
        return this.getPseudo().length();
    }
}
