package com.paperboard.server;

import com.paperboard.Error.PaperBoardAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

/**
 * Controller to manage httpRequests about PaperBoard
 */
@CrossOrigin(origins = "*")
//@CrossOrigin("*")
@RestController("/paperboard")
public class PaperBoardController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    public PaperBoardController() {

    }

    /**
     * @return the set of paperboards
     */
    @RequestMapping(value = "/paperboards", method = RequestMethod.GET)
    public HashSet<PaperBoard.PaperBoardInfo> getAllPaperBoard() {
        final HashSet<PaperBoard.PaperBoardInfo> paperBoardsInfo = new HashSet<>();
        final Iterator<PaperBoard> paperboards = ServerApplication.getInstance().getPaperBoards().iterator();
        while (paperboards.hasNext()) {
            final PaperBoard paperBoard = paperboards.next();
            paperBoardsInfo.add(paperBoard.getInfo());
        }
        return paperBoardsInfo;
    }

    // TODO documentation
    @RequestMapping(value = "/paperboard", method = RequestMethod.GET)
    public PaperBoard getPaperBoard(@RequestParam(value = "title") final String title) throws
            PaperBoardAlreadyExistException {
        return ServerApplication.getPaperBoard(title);
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
                                     @RequestParam(value = "backgroundImage") final Optional<String> backgroundImage) throws
            PaperBoardAlreadyExistException {
        final PaperBoard paperBoard;
        if (!backgroundColor.isEmpty() || !backgroundImage.isEmpty()) {
            paperBoard = new PaperBoard(title, backgroundColor, backgroundImage);
        } else {
            paperBoard = new PaperBoard(title);
        }

        ServerApplication.addPaperBoard(paperBoard);
        return paperBoard;
    }

    @PostMapping(value = "/paperboard/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFile(@RequestParam final String paperboardName,
                                     @RequestParam final MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                final String uploadsDir = "/uploads/";
                final String realPathtoUploads = request.getServletContext().getRealPath(uploadsDir);
                if (!new File(realPathtoUploads).exists()) {
                    new File(realPathtoUploads).mkdir();
                }

                final String orgName = file.getOriginalFilename();
                final String newName = paperboardName.concat(orgName.substring(orgName.lastIndexOf("."),
                        orgName.length()));
                final String filePath = realPathtoUploads + newName;
                final File dest = new File(filePath);
                file.transferTo(dest);
                ServerApplication.addBackgroundImage(paperboardName, filePath);
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.ok().build();

    }

    @RequestMapping("/paperboard/download/image")
    public ResponseEntity downloadImage(@RequestParam final String paperboardName) throws IOException {
        final String imagePath = ServerApplication.getBackgroundImagePath(paperboardName);
        final File file = new File(imagePath);
        final InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(file.length())
                .body(resource);
    }
}
