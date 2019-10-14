package com.paperboard.Error;

import com.paperboard.server.PaperBoard;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "A paperBoard with the same name already exists")
public class PaperBoardAlreadyExistException extends RuntimeException {

    public PaperBoardAlreadyExistException(final PaperBoard paperBoard) {
        super("PaperBoard : " + paperBoard.getTitle() + " already exists");
    }
}
