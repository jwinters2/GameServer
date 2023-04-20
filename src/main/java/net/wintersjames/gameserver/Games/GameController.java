package net.wintersjames.gameserver.Games;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import net.wintersjames.gameserver.CookieUtils;
import net.wintersjames.gameserver.Games.GameDao.GameMatchPersistenceService;
import net.wintersjames.gameserver.Games.GameDao.PlayerToMatchService;
import net.wintersjames.gameserver.HTTPUtils;
import net.wintersjames.gameserver.Session.ListenToDisconnects;
import net.wintersjames.gameserver.Session.SessionState;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.Session.WebSocketSessionManager;
import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource(value = "classpath:webpath.properties")
public class GameController implements ListenToDisconnects {
	
	@Value("${context-root}")
	private String contextRoot;
	
	Logger logger = LoggerFactory.getLogger(GameController.class);
    
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
		
		model.addAttribute("contextRoot", contextRoot);
   
        logger.info("message received for game " + game);
        
        String id = CookieUtils.getSessionCookie(request, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
        
        GameMatch match = matchManager.getMatch(uid, matchid);
		logger.info("\n{}", match.getGameState(uid));
		if(HTTPUtils.redirectIfNotLoggedIn(uid, response, contextRoot + "/login")) {
			return "login";
		}
		
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
   
        logger.info("message received for game {}", game);
        
        String message = HtmlUtils.htmlEscape(URLDecoder.decode(request.getParameter("message"), StandardCharsets.UTF_8));
        logger.info(message);
        
        String id = CookieUtils.getSessionCookie(request, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
		if(HTTPUtils.redirectIfNotLoggedIn(uid, response, contextRoot + "/login")) {
			return "failed to send message: user is not logged in";
		}
        
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
        
        String destination = contextRoot + "/websocket/chat/${game}/${matchid}/${userid}"
                .replace("${game}", match.getGame().getSimpleName().toLowerCase())
                .replace("${matchid}", Long.toString(match.getId()))
                .replace("${userid}", Integer.toString(uid));
        
		logger.info("sending chat to {}",destination);
        simpMessageTemplate.convertAndSend(destination, payload);
    }
	
	@PostMapping("/game/{game}/{matchid}/move")
	@ResponseBody
    public String receiveMove(
			@PathVariable(name="game") String game, 
			@PathVariable(name="matchid") long matchid, 
			HttpServletRequest request, 
			HttpServletResponse response) {
		String id = CookieUtils.getSessionCookie(request, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
		if(HTTPUtils.redirectIfNotLoggedIn(uid, response, contextRoot + "/login")) {
			return "login";
		}
        
        GameMatch match = matchManager.getMatch(uid, matchid);
        if(match != null) {
			boolean success = match.handleMove(uid, request);
			logger.info("move {}", (success ? "succeeded" : "failed"));
			if(success) {
				
				// send updates to each player
				for(int pid: match.getPlayers()) {
					updateGameForUsers(match, pid);
				}
				
				if(match.getGameState(uid).getStatus() == GameState.Status.INCOMPLETE) {

					// persist to DB if unfinished
					boolean saved = matchPersistenceService.saveMatch(match);
					if(saved) {
						for(int pid: match.getPlayers()) {
							ptmService.savePlayerToMatch(pid, matchid);
						}
					} else {
						logger.info("match failed to save");
					}
					
				} else {
					// delete from DB when finished
					// TODO?: archive instead
					matchPersistenceService.deleteMatch(matchid);
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
		String id = CookieUtils.getSessionCookie(request, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
		if(HTTPUtils.redirectIfNotLoggedIn(uid, response, contextRoot + "/login")) {
			return "login";
		}
        
        GameMatch match = matchManager.getMatch(uid, matchid);
        if(match != null) {
			updateGameForUsers(match, uid);
			return "update sent";
		}
		
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		return "error: no match found";
	}
	
	@GetMapping("/game/{game}/{matchid}/leavegame")
    public String leaveGame(            
			@PathVariable(name="game") String game, 
            @PathVariable(name="matchid") long matchid, 
            Model model,
            HttpServletRequest request, 
            HttpServletResponse response) {
   
        logger.info("leavegame request received for game " + game);
		
		String id = CookieUtils.getSessionCookie(request, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
		if(HTTPUtils.redirectIfNotLoggedIn(uid, response, contextRoot + "/login")) {
			return "login";
		}
		
		User user = userService.findByUid(uid);
		String reason = "Player " + user.getUsername() + " has left the game. (player left)<br><i>Game can be continued later.</i>";
        
        List<GameMatch> matches = matchManager.getMatchesByUid(uid);
		for(GameMatch match: matches) {
			for(int playerid: match.getPlayers()) {
				if(uid != playerid) {
					endGame(match, playerid, reason);
				}
			}
		}
        
		try {
			response.sendRedirect(contextRoot + "/homepage");
		} catch (IOException e) {
			logger.error("leavegame failed to send redirect for uid={}, game={}, matchid={}", uid, game, matchid);
		}
        return "homepage";
    }
	
	@GetMapping("/game/{game}/{matchid}/resign")
    public String resign(            
			@PathVariable(name="game") String game, 
            @PathVariable(name="matchid") long matchid, 
            Model model,
            HttpServletRequest request, 
            HttpServletResponse response) {
   
        logger.info("resign request received for game " + game);
		
		String id = CookieUtils.getSessionCookie(request, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
		if(HTTPUtils.redirectIfNotLoggedIn(uid, response, contextRoot + "/login")) {
			return "login";
		}
		
		User user = userService.findByUid(uid);
		String reason = "Player " + user.getUsername() + " has left the game. (player resigned)";
		
        List<GameMatch> matches = matchManager.getMatchesByUid(uid);
		for(GameMatch m: matches) {
			for(int playerid: m.getPlayers()) {
				if(uid != playerid) {
					endGame(m, playerid, reason);
				}
			}
		}
		
		GameMatch match = matchManager.getMatch(uid, matchid);
		match.resign(uid);
		matchManager.removeMatch(matchid);
        
		try {
			response.sendRedirect(contextRoot + "/homepage");
		} catch (IOException e) {
			logger.error("leavegame failed to send redirect for uid={}, game={}, matchid={}", uid, game, matchid);
		}
        return "homepage";
    }
	
	public void updateGameForUsers(GameMatch match, @DestinationVariable("uid") int uid) {
        GameState payload = match.getGameState(uid);
        
        String destination = contextRoot + "/websocket/game/${game}/${matchid}/${userid}"
                .replace("${game}", match.getGame().getSimpleName().toLowerCase())
                .replace("${matchid}", Long.toString(match.getId()))
                .replace("${userid}", Integer.toString(uid));
        
        logger.info("sending game update to {}", destination);
        simpMessageTemplate.convertAndSend(destination, payload);
    }
	
	public void endGame(GameMatch match, @DestinationVariable("uid") int uid, String reason) {
        JSONObject payload = new JSONObject();
		payload.put("type", "gameEnd");
		payload.put("reason", reason);
		logger.info("payload: {}", payload);
        
        String destination = contextRoot + "/websocket/game/${game}/${matchid}/${userid}"
                .replace("${game}", match.getGame().getSimpleName().toLowerCase())
                .replace("${matchid}", Long.toString(match.getId()))
                .replace("${userid}", Integer.toString(uid));
        
        logger.info("sending game update to {}", destination);
        simpMessageTemplate.convertAndSend(destination, payload.toString());
    }
    
    @Override
    public void handleDisconnects(int uid) {
		logger.info("handle disconnect for user {}",uid);
		
		User user = userService.findByUid(uid);
		String reason = "Player " + user.getUsername() + " has left the game. (lost connection)<br><i>Game can be continued later.</i>";
		
		List<GameMatch> matches = matchManager.getMatchesByUid(uid);
		for(GameMatch match: matches) {
			for(int playerid: match.getPlayers()) {
				if(uid != playerid) {
					endGame(match, playerid, reason);
				}
			}
		}
    }
    
}
