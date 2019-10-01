package com.paperboard.server;

import java.util.concurrent.atomic.AtomicLong;

public class User {
    private static AtomicLong idCounter = new AtomicLong(0);
    private final String id;

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    private String pseudo;

    public User(String pseudo) {
        this.id = String.valueOf(idCounter.getAndIncrement());
        this.pseudo = pseudo;
    }

    public void joinPaperBoard() {
    }

    @Override
    public String toString() {
        return this.pseudo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false;
        }
        User user = (User) obj;
        return this.getPseudo().equals(user.getPseudo());
    }

    @Override
    public int hashCode() {
        return this.getPseudo().length();
    }
}
