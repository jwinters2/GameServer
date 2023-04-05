/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import net.wintersjames.gameserver.Games.GameUtils;
import net.wintersjames.gameserver.Games.Queue.GameQueue;
import net.wintersjames.gameserver.Games.Queue.GameQueueManager;
import net.wintersjames.gameserver.Games.Queue.GameQueueUpdate;
import net.wintersjames.gameserver.Session.SessionState;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
public class QueueController {
    
    @Autowired
    private GameQueueManager queueManager;
    
    @Autowired
    SessionStateManager sessionManager;
    
    @Autowired
    UserService userService;
    
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
        model.addAttribute("game", gameTitle);
        model.addAttribute("users", users);
        
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
        
        updateQueue(queue);
        
        return "";
    }
    
    @MessageMapping("/queue")
    @SendTo("/queue/challenge")
    public GameQueueUpdate updateQueue(GameQueue gameQueue) {
        return new GameQueueUpdate(gameQueue);
    }
}
