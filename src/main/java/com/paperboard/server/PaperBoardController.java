package com.paperboard.server;

import com.paperboard.Error.PaperBoardAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Optional;

/**
 * Controller to manage httpRequests about PaperBoard
 */
@RestController("/paperboard")
public class PaperBoardController {

    @Autowired
    public PaperBoardController() {

    }

    /**
     * @return the set of paperboards
     */
    @RequestMapping(value = "/paperboard", method = RequestMethod.GET)
    public HashSet<PaperBoard> getAllPaperBoard() {
        return ServerApplication.getInstance().getPaperBoards();
    }

    /**
     * Receive the request to create a new PaperBoard on /paperboard?title=...&...
     * If backgroundColor and backgroundImage is specified the color is prioritized,
     * if nothing is specified a default color is given.
     *
     * @param title           Mandatory title of the paperBoard, cannot be the same as another paperBoard
     * @param backgroundColor Of the paperBoard, white if not specified
     * @param backgroundImage Of the paperBoard
     * @throws PaperBoardAlreadyExistException thrown if the user try to create a paperBoard with the same name
     */
    @RequestMapping(value = "/paperboard", method = RequestMethod.POST)
    public PaperBoard postPaperBoard(@RequestParam(value = "title") final String title,
                                     @RequestParam(value = "backgroundColor") final Optional<String> backgroundColor,
                                     @RequestParam(value = "backgroundImage") final Optional<byte[]> backgroundImage) throws PaperBoardAlreadyExistException {
        final PaperBoard paperBoard;
        if (!backgroundColor.isEmpty()) {
            paperBoard = new PaperBoard(title, backgroundColor.get());
        } else if (!backgroundImage.isEmpty()) {
            paperBoard = new PaperBoard(title, backgroundImage.get());
        } else {
            paperBoard = new PaperBoard(title);
        }

        ServerApplication.addPaperBoard(paperBoard);
        return paperBoard;
    }
}
