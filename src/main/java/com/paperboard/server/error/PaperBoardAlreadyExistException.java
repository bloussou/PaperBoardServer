package com.paperboard.server.error;

import com.paperboard.server.PaperBoard;

public class PaperBoardAlreadyExistException extends RuntimeException {

    public PaperBoardAlreadyExistException(final PaperBoard paperBoard) {
        super("PaperBoard : " + paperBoard.getTitle() + " already exists");
    }
}
