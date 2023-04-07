/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.wintersjames.gameserver.CookieUtils;
import net.wintersjames.gameserver.Session.ListenToDisconnects;
import net.wintersjames.gameserver.Session.SessionState;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.Session.WebSocketSessionManager;
import net.wintersjames.gameserver.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

/**
 *
 * @author james
 */
@Controller
public class GameController implements ListenToDisconnects {
    
    @Autowired
    protected SessionStateManager sessionManager;
    
    @Autowired
    protected UserService userService;
    
    @Autowired
    private GameMatchManager matchManager;
    
    @Autowired
    private SimpMessagingTemplate simpMessageTemplate;
       
    private WebSocketSessionManager webSocketManager;
    
    @Autowired
    public GameController(WebSocketSessionManager webSocketManager) {
        this.webSocketManager = webSocketManager;
        this.webSocketManager.registerListener(this);
    }
    
    @GetMapping("/game/{game}/{matchid}")
    public String getGamePage(
            @PathVariable(name="game") String game, 
            @PathVariable(name="matchid") long matchid, 
            Model model,
            HttpServletRequest request, 
            HttpServletResponse response) {
   
        System.out.println("message received for game " + game);
        
        String id = CookieUtils.getSessionCookie(request);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
        
        matchManager.getMatch(uid, matchid);
        model.addAttribute("matchid", matchid);
        model.addAttribute("game", game);
        model.addAttribute("myuid", uid);
        
        return "game";
    }
    
    // handle game 
    public void updateStateToUsers() {
        
    }
    
    public void getStateUpdate() {
        
    }
    
    @PostMapping("/game/{game}/{matchid}/chat")
    @ResponseBody
    public String receiveChatMessage(@PathVariable(name="game") String game, @PathVariable(name="matchid") long matchid, HttpServletRequest request, HttpServletResponse response) {
   
        System.out.println("message received for game " + game);
        
        String message = HtmlUtils.htmlEscape(URLDecoder.decode(request.getParameter("message"), StandardCharsets.UTF_8));
        System.out.println(message);
        
        String id = CookieUtils.getSessionCookie(request);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
        
        GameMatch match = matchManager.getMatch(uid, matchid);
        if(match != null && message.length() > 0) {
            match.newMessage(uid, message);
            for(int playerid: match.getPlayers()) {
                updateChatForUsers(match, playerid);
            }
        }
        
        return "(game) message failed to send";
    }
    
    public void updateChatForUsers(GameMatch match, @DestinationVariable("uid") int uid) {
        List<ChatMessage> payload = match.getChatLog();
        
        String destination = "/websocket/chat/${game}/${matchid}/${userid}"
                .replace("${game}", match.getGame().getSimpleName().toLowerCase())
                .replace("${matchid}", Long.toString(match.getId()))
                .replace("${userid}", Integer.toString(uid));
        
        System.out.println("sending to " + destination);
        simpMessageTemplate.convertAndSend(destination, payload);
    }
    
    @Override
    public void handleDisconnects(int uid) {
    }
    
}
