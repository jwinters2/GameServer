/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import net.wintersjames.gameserver.Games.GameUtils;
import net.wintersjames.gameserver.Games.Queue.GameQueueManager;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        
        queueManager.enqueueUser(user, GameUtils.getClassFromName(game));
        List<User> users = queueManager.getQueue(GameUtils.getClassFromName(game));
        users.remove(user);
        
        String gameTitle = game.substring(0, 1).toUpperCase() + game.substring(1).toLowerCase();
        model.addAttribute("game", gameTitle);
        model.addAttribute("users", users);
        
        return "queue";
    }
}
