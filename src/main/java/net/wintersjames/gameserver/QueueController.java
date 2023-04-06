/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import net.wintersjames.gameserver.Games.GameUtils;
import net.wintersjames.gameserver.Games.Queue.GameQueue;
import net.wintersjames.gameserver.Games.Queue.GameQueueManager;
import net.wintersjames.gameserver.Games.Queue.GameQueueUpdate;
import net.wintersjames.gameserver.Session.ListenToDisconnects;
import net.wintersjames.gameserver.Session.SessionState;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.Session.WebSocketSessionManager;
import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 *
 * @author james
 */
@Controller
public class QueueController implements ListenToDisconnects {
    
    @Autowired
    private GameQueueManager queueManager;
    
    @Autowired
    private SessionStateManager sessionManager;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SimpMessagingTemplate simpMessageTemplate;
    
    private WebSocketSessionManager webSocketManager;
    
    @Autowired
    public QueueController(WebSocketSessionManager webSocketManager) {
        this.webSocketManager = webSocketManager;
        this.webSocketManager.registerListener(this);
    }
    
    @GetMapping("/queue/{game}")
    public String homePage(
            @PathVariable("game") String game, 
            Model model, 
            HttpServletRequest request, 
            HttpServletResponse response) {     

        String id = CookieUtils.getSessionCookie(request, response);
        int uid = sessionManager.getSessionState(id).getLoginState().getUid();      
        User user = userService.findByUid(uid);
        
        GameQueue queue = queueManager.enqueueUser(user, GameUtils.getClassFromName(game));
        sessionManager.getSessionState(id).setGameQueue(queue);
        
        List<User> users = queueManager.getQueue(GameUtils.getClassFromName(game));
        users.remove(user);
        
        String gameTitle = game.substring(0, 1).toUpperCase() + game.substring(1).toLowerCase();
        model.addAttribute("myuid", uid);
        model.addAttribute("game", gameTitle);
        model.addAttribute("users", users);
        
        // update the list of everyone else in the queue
        for(User u: users) {
            updateQueues(u.getUid(), queue);
        }
        
        return "queue";
    }
    
    @GetMapping("queue/challenge")
    @ResponseBody
    public String challengeUser(@RequestParam(name="uid") int to_uid, HttpServletRequest request) {
        
        String id = CookieUtils.getSessionCookie(request);
        SessionState state = sessionManager.getSessionState(id);
        int from_uid = state.getLoginState().getUid();
        GameQueue queue = state.getGameQueue();
        
        queue.challengeUser(from_uid, to_uid);

        return "";
    }
    
    @MessageMapping("/challenge/{uid}")
    @SendTo("/websocket/queue/{uid}")
    public String handleWebsocketMessage(@DestinationVariable("uid") int uid) {        
        String message = "challenging user " + Integer.toString(uid);
        System.out.println(message);
        return message;
    }
    
    public void updateQueues(@DestinationVariable("uid") int uid, GameQueue queue) {
        System.out.println("updating " + Integer.toString(uid));
        GameQueueUpdate payload = new GameQueueUpdate(queue, uid);
        
        simpMessageTemplate.convertAndSend(
            "/websocket/queue/" + Integer.toString(uid),
             payload);
    }

    @Override
    public void handleDisconnects(int uid) {
        System.out.println("queue controller handling disconnected user " + Integer.toString(uid));
        
        User user = userService.findByUid(uid);    
        GameQueue queue = sessionManager.getUserSession(uid).getGameQueue();
        queue.remove(user);
        
        List<User> users = queueManager.getQueue(queue.getGame());
        
        for(User u: users) {
            updateQueues(u.getUid(), queue);
        }
    }
}
