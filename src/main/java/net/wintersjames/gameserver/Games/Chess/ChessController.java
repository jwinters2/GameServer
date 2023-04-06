/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Chess;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.wintersjames.gameserver.CookieUtils;
import net.wintersjames.gameserver.Games.GameController;
import net.wintersjames.gameserver.Games.GameMatch;
import net.wintersjames.gameserver.Session.SessionState;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author james
 */
@Controller
public class ChessController extends GameController {

    public ChessController() {
        this.setMatch(new ChessMatch());
    }

    @Override
    public void updateStateToUsers() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void getStateUpdate() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void handleDisconnects(int uid) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    @GetMapping("/game/chess/chat/{matchid}")
    @ResponseBody
    public String receiveChatMessage(@PathVariable(name="matchid") long matchid, HttpServletRequest request, HttpServletResponse response) {
        return super.receiveChatMessage(matchid, request, response);
    }
    
}
