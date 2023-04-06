/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import net.wintersjames.gameserver.Session.SessionStateManager;

import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 *
 * @author james
 */

@Controller
public class LoginController {
    
    @Autowired
    private SessionStateManager sessionManager;
    
    @Autowired
    UserService userService;
    
    @GetMapping("/login")
    public String homePage(Model model, HttpServletRequest request, HttpServletResponse response) {             
        return "login";
    }
    
    @PostMapping("/login")
    @ResponseBody
    public String register(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        
        
        String id = CookieUtils.getSessionCookie(request, response);
        
        String username = request.getParameter("username");
        String password_hash = request.getParameter("password_hash");
        
        User user = userService.findByUsername(username);
        String db_hash = user.getPasswordHash();
        
        if(password_hash.equals(db_hash)) {
            // success, add user info to session state
            sessionManager.getSessionState(id).login(user);
            return "/homepage";
        } else {
            // bad password
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "Incorrect username/password";
        }
    }    
}
