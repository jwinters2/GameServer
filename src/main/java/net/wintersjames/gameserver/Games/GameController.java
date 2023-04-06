/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import net.wintersjames.gameserver.CookieUtils;
import net.wintersjames.gameserver.Session.ListenToDisconnects;
import net.wintersjames.gameserver.Session.SessionState;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.Session.WebSocketSessionManager;
import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public abstract class GameController implements ListenToDisconnects {
    
    @Autowired
    protected SessionStateManager sessionManager;
    
    @Autowired
    protected UserService userService;
    
    private ArrayList<GameMatch> activeMatches;
       
    private WebSocketSessionManager webSocketManager;
    
    @Autowired
    public GameController(WebSocketSessionManager webSocketManager) {
        this.webSocketManager = webSocketManager;
        this.webSocketManager.registerListener(this);
        this.activeMatches = new ArrayList<>();
    }
    
    // handle game 
    public abstract void updateStateToUsers();
    
    public abstract void getStateUpdate();
    
    //@GetMapping("/game/chat/{matchid}")
    //@ResponseBody
    public String receiveChatMessage(@PathVariable(name="matchid") long matchid, HttpServletRequest request, HttpServletResponse response) {
   
        System.out.println("(game) message received");
        
        String id = CookieUtils.getSessionCookie(request);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
        
        return "(game) message received";
    }
    
}
