package net.wintersjames.gameserver.Queue;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.wintersjames.gameserver.CookieUtils;
import net.wintersjames.gameserver.Games.GameDao.GameMatchPersistenceService;
import net.wintersjames.gameserver.Games.GameDao.PlayerToMatchService;
import net.wintersjames.gameserver.Games.GameMatchManager;
import net.wintersjames.gameserver.Games.GameUtils;
import net.wintersjames.gameserver.Queue.GameInvite;
import net.wintersjames.gameserver.Queue.GameQueue;
import net.wintersjames.gameserver.Queue.GameQueueManager;
import net.wintersjames.gameserver.Queue.GameQueueUpdate;
import net.wintersjames.gameserver.HTTPUtils;
import net.wintersjames.gameserver.Session.ListenToDisconnects;
import net.wintersjames.gameserver.Session.SessionState;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.Session.WebSocketSessionManager;
import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.json.JSONArray;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author james
 */
@Controller
@PropertySource(value = "classpath:webpath.properties")
public class QueueController implements ListenToDisconnects {
	
	Logger logger = LoggerFactory.getLogger(QueueController.class);
	
	@Value("${context-root}")
	private String contextRoot;
    
    @Autowired
    private GameQueueManager queueManager;
    
    @Autowired
    private SessionStateManager sessionManager;
    
    @Autowired
    private UserService userService;
	
	@Autowired
	private GameMatchPersistenceService matchPersistenceService;
	
	@Autowired
	private PlayerToMatchService ptmService;
    
    @Autowired
    private SimpMessagingTemplate simpMessageTemplate;
    
    @Autowired
    private GameMatchManager matchManager;
    
    private WebSocketSessionManager webSocketManager;
    
    @Autowired
    public QueueController(WebSocketSessionManager webSocketManager) {
        this.webSocketManager = webSocketManager;
        this.webSocketManager.registerListener(this);
    }
    
    @GetMapping("/queue/{game}")
    public String queuePage(
            @PathVariable("game") String game, 
            Model model, 
            HttpServletRequest request, 
            HttpServletResponse response) {     
		
		model.addAttribute("contextRoot", contextRoot);

        String id = CookieUtils.getSessionCookie(request, response, sessionManager);
        int uid = sessionManager.getSessionState(id).getLoginState().getUid();
		if(HTTPUtils.redirectIfNotLoggedIn(uid, response, contextRoot + "/login")) {
			return "login";
		}
        User user = userService.findByUid(uid);
        
        GameQueue queue = queueManager.enqueueUser(user, GameUtils.getClassFromName(game));
        sessionManager.getSessionState(id).setGameQueue(queue);
        
        List<User> users = queueManager.getQueue(GameUtils.getClassFromName(game));
        users.remove(user);
		
		List<Long> pendingMatchIds = ptmService.getMatches(uid);
		HashMap<Long, List<Integer>> pendingMatches = new HashMap<>();
		for(long matchid: pendingMatchIds) {
			List<Integer> players = matchPersistenceService.getPlayersFromMatchId(matchid);
			pendingMatches.put(matchid, players);
		}
	        
        String gameTitle = game.substring(0, 1).toUpperCase() + game.substring(1).toLowerCase();
        model.addAttribute("myuid", uid);
        model.addAttribute("game", gameTitle);
        model.addAttribute("users", new JSONArray(clientSafe(users)));
		model.addAttribute("pendingMatches", new JSONObject(pendingMatches));
        
        // update the list of everyone else in the queue
        for(User u: users) {
			if(u != null) {
				updateQueues(u.getUid(), queue);
			}
        }
        
        return "queue";
    }
    
    @GetMapping("/queue/challenge/{uid}")
    @ResponseBody
    public String challengeUser(@PathVariable(name="uid") int to_uid, 
			@RequestParam(name="continue", required = false) Long continueMatchId, 
			HttpServletRequest request,
			HttpServletResponse response) {
        
        String id = CookieUtils.getSessionCookie(request, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
        int from_uid = state.getLoginState().getUid();
		if(HTTPUtils.redirectIfNotLoggedIn(from_uid, response, contextRoot + "/login")) {
			return "login";
		}
        GameQueue queue = state.getGameQueue();
        
        queue.challengeUser(from_uid, to_uid, continueMatchId);
        
        updateForUser(from_uid, queue);
        updateForUser(to_uid, queue);

        return "invite sent";
    }
    
    @GetMapping("/queue/accept/{inviteid}")
    @ResponseBody
    public String acceptInvite(
			@PathVariable(name="inviteid") long timestamp, 
			HttpServletRequest request, 
			HttpServletResponse response) {
        
        logger.info("accept invite");
        
        String id = CookieUtils.getSessionCookie(request, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
		if(HTTPUtils.redirectIfNotLoggedIn(uid, response, contextRoot + "/login")) {
			return "login";
		}
        GameQueue queue = state.getGameQueue();
        
        logger.info("uid: {}", uid);
        
        GameInvite invite = queue.getInvite(timestamp);
        logger.info("invite: {}", invite);
        if(invite != null && invite.getToUid() == uid) {
            // setup a new game
            boolean inviteSuccess = queue.startGame(invite, matchManager);
            if(inviteSuccess) {
                sendToGame(invite.getFromUid(), invite);
                sendToGame(invite.getToUid(), invite);
                return "invite accepted"; 
            }
        }

        return "invite accept failed";
    }
    
    @GetMapping("/queue/cancel/{inviteid}")
    @ResponseBody
    public String cancelInvite(
			@PathVariable(name="inviteid") long timestamp, 
			HttpServletRequest request,
			HttpServletResponse response) {
        
        String id = CookieUtils.getSessionCookie(request, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
        int uid = state.getLoginState().getUid();
		if(HTTPUtils.redirectIfNotLoggedIn(uid, response, contextRoot + "/login")) {
			return "login";
		}
        GameQueue queue = state.getGameQueue();
        
        GameInvite invite = queue.getInvite(timestamp);
        if(invite.includesUser(uid)) {
            queue.removeInvite(timestamp);
            updateForUser(uid, queue);
        }

        return "invite canceled";
    }
    
    public void updateQueues(@DestinationVariable("uid") int recipient_uid, GameQueue queue) {
        logger.info("updating {}",recipient_uid);
        GameQueueUpdate payload = new GameQueueUpdate(queue);
        payload.cleanForUser(recipient_uid);
		
		logger.info("sending update to {}",recipient_uid);
        
        simpMessageTemplate.convertAndSend(
            contextRoot + "/websocket/queue/" + Integer.toString(recipient_uid),
             payload);
    }
    
    // user ${uid} has either joined or left, notify the others
    private void updateForUser(int uid) {
        User user = userService.findByUid(uid);    
        GameQueue queue = sessionManager.getUserSession(uid).getGameQueue();
		if(queue != null) {
			queue.remove(user);     
			updateForUser(uid, queue);
		}
    }
       
    // user ${uid} has either joined or left, notify the others
    private void updateForUser(int uid, GameQueue queue) {
        
        List<User> users = queueManager.getQueue(queue.getGame());
        
        for(User u: users) {
            updateQueues(u.getUid(), queue);
        } 
    }
    
    public void sendToGame(@DestinationVariable("uid") int recipient_uid, GameInvite invite) {
        logger.info("sending to game {}", recipient_uid);
        
        simpMessageTemplate.convertAndSend(
            contextRoot + "/websocket/queue/" + Integer.toString(recipient_uid),
             invite);
    }
	
	private List<User> clientSafe(List<User> users) {
		List<User> retval = new ArrayList<>();
		for(User user: users) {
			if(user != null) {
				retval.add(user.clientSafe());
			}
		}
		return retval;
	}

    @Override
    public void handleDisconnects(int uid) {
        updateForUser(uid);
    }
}
