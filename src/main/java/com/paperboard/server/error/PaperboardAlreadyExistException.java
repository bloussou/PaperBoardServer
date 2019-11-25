package com.paperboard.server.error;

import com.paperboard.server.Paperboard;

public class PaperboardAlreadyExistException extends RuntimeException {

    public PaperboardAlreadyExistException(final Paperboard paperboard) {
        super("Paperboard : " + paperboard.getTitle() + " already exists");
    }
}
