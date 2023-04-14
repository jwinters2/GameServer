package net.wintersjames.gameserver.Games;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.wintersjames.gameserver.CookieUtils;
import net.wintersjames.gameserver.Games.GameDao.GameMatchPersistenceService;
import net.wintersjames.gameserver.Games.GameDao.PlayerToMatchService;
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
	private GameMatchPersistenceService matchPersistenceService;
	
	@Autowired
	private PlayerToMatchService ptmService;
    
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
        
        GameMatch match = matchManager.getMatch(uid, matchid);
		System.out.println(match.getGameState(uid));
		
        model.addAttribute("matchid", matchid);
        model.addAttribute("game", game);
        model.addAttribute("myuid", uid);
		
		model.addAllAttributes(match.getAttributes(uid));
        
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
			return "message sent";
        }
        
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return "(game) message failed to send";
    }
    
    public void updateChatForUsers(GameMatch match, @DestinationVariable("uid") int uid) {
        List<ChatMessage> payload = match.getChatLog();
        
        String destination = "/websocket/chat/${game}/${matchid}/${userid}"
                .replace("${game}", match.getGame().getSimpleName().toLowerCase())
                .replace("${matchid}", Long.toString(match.getId()))
                .replace("${userid}", Integer.toString(uid));
        
        System.out.println("sending chat to " + destination);
        simpMessageTemplate.convertAndSend(destination, payload);
    }
	
	@PostMapping("/game/{game}/{matchid}/move")
	@ResponseBody
    public String receiveMove(
			@PathVariable(name="game") String game, 
			@PathVariable(name="matchid") long matchid, 
			HttpServletRequest request, 
			HttpServletResponse response) {
		String id = CookieUtils.getSessionCookie(request);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
        
        GameMatch match = matchManager.getMatch(uid, matchid);
        if(match != null) {
			boolean success = match.handleMove(uid, request);
			System.out.println("move " + (success ? "succeeded" : "failed"));
			if(success) {
				
				// send updates to each player
				for(int pid: match.getPlayers()) {
					updateGameForUsers(match, pid);
				}
				
				// persist to DB
				boolean saved = matchPersistenceService.saveMatch(match);
				if(saved) {
					for(int pid: match.getPlayers()) {
						ptmService.savePlayerToMatch(pid, matchid);
					}
				} else {
					System.out.println("match failed to save");
				}
				
				return "success";
			}
		}
		
		response.setStatus(400);
		return "bad request";
	}
	
	@GetMapping("/game/{game}/{matchid}/state")
	@ResponseBody
    public String sendState(
			@PathVariable(name="game") String game, 
			@PathVariable(name="matchid") long matchid, 
			HttpServletRequest request, 
			HttpServletResponse response) {
		String id = CookieUtils.getSessionCookie(request);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
        
        GameMatch match = matchManager.getMatch(uid, matchid);
        if(match != null) {
			updateGameForUsers(match, uid);
			return "update sent";
		}
		
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return "error: no match found";
	}
	
	public void updateGameForUsers(GameMatch match, @DestinationVariable("uid") int uid) {
        GameState payload = match.getGameState(uid);
        
        String destination = "/websocket/game/${game}/${matchid}/${userid}"
                .replace("${game}", match.getGame().getSimpleName().toLowerCase())
                .replace("${matchid}", Long.toString(match.getId()))
                .replace("${userid}", Integer.toString(uid));
        
        System.out.println("sending game update to " + destination);
        simpMessageTemplate.convertAndSend(destination, payload);
    }
    
    @Override
    public void handleDisconnects(int uid) {
		System.out.println("handle disconnect for user " + Integer.toString(uid));
    }
    
}
