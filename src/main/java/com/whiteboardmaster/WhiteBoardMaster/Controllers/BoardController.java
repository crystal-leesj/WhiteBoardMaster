package com.whiteboardmaster.WhiteBoardMaster.Controllers;

import com.whiteboardmaster.WhiteBoardMaster.Models.*;
import com.whiteboardmaster.WhiteBoardMaster.Models.ApplicationUserRepository;
import com.whiteboardmaster.WhiteBoardMaster.Models.Board;
import com.whiteboardmaster.WhiteBoardMaster.Models.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.security.Principal;


@Controller
public class BoardController {

    @Autowired
    ApplicationUserRepository userRepository;

    @Autowired
    BoardRepository boardRepository;

    /*
                    POST ROUTES
    */

    static {
        System.setProperty("java.awt.headless", "false");
    }

    @PostMapping("/createAndSaveBoard")
    public String createAndSaveBoard(Model m, Principal p, String problemDomain, String algorithm, String pseudoCode, String bigOTimeNotation, String verification, String code, String edgeCases, String inputAndOutput, String visual, String title) throws IOException {

        Board newBoard = new Board(problemDomain, algorithm, pseudoCode, bigOTimeNotation, bigOTimeNotation, verification, code, edgeCases, inputAndOutput, visual, title);
        newBoard.toMarkDown();
        m.addAttribute("board", newBoard);

        if (p != null) {
            // get user and save board to their account
            ApplicationUser user = userRepository.findByUserName(p.getName());
            newBoard.setApplicationUser(user);
            boardRepository.save(newBoard);
            user.addBoard(newBoard);
            userRepository.save(user);
        }

        this.addUserNameToPage(p, m);
        return "result";
    }


    /*
                    GET ROUTES
    */
    @GetMapping("/board/{id}")
    public String getBoardFromUserProfile(@PathVariable long id, Principal p, Model m) throws IOException {

        Board boardFromDataBase = boardRepository.getOne(id);
        m.addAttribute("board", boardFromDataBase);
        this.addUserNameToPage(p, m);

        boardFromDataBase.toMarkDown();

        return "result";
    }

    @GetMapping("/build")
    public String displayWhiteboardForm(Principal p, Model m) {

        this.addUserNameToPage(p, m);

        return "whiteboard";
    }

    private void addUserNameToPage(Principal p, Model m) {
        if (p != null) {
            m.addAttribute("username", p.getName());
        } else {
            m.addAttribute("username", "New user");
        }
    }
}
