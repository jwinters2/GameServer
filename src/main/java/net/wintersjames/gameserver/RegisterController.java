/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.wintersjames.gameserver.Session.SessionState;
import net.wintersjames.gameserver.Session.LoginState;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author james
 */
@Controller
public class RegisterController {
    
    final String charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    
    @Autowired
    SessionStateManager sessionManager;
    
    @Autowired
    UserService userService;
    
    @GetMapping("/getsalt")
    @ResponseBody
    public String getSalt(HttpServletRequest request, HttpServletResponse response) {
        String salt = "";
        for(int i=0; i<20; i++) {
            salt += charset.charAt((int) Math.floor(Math.random() * charset.length()));
        }
        
        String id = CookieUtils.getSessionCookie(request, response);
        SessionState state = sessionManager.getSessionState(id);
        if(state.getLoginState().getSalt() != null) {
            return state.getLoginState().getSalt();
        }
        
        state.getLoginState().setSalt(salt);
        
        return salt;
    }
    
    @GetMapping("/getsaltfromuser")
    @ResponseBody
    public String getSalt(@RequestParam(required = false) String username, HttpServletRequest request, HttpServletResponse response) {
        User user = userService.findByUsername(username);    
        if(user == null)
        {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return "User with name " + username + " already exists";
        }
        return user.getSalt();
    }
    
    @PostMapping("/register")
    @ResponseBody
    public String register(Model model, HttpServletRequest request, HttpServletResponse response) {
        
        
        String id = CookieUtils.getSessionCookie(request, response);
        
        String username = request.getParameter("username");
        String password_hash = request.getParameter("password_hash");
        String salt = sessionManager.getSessionState(id).getLoginState().getSalt();

        boolean result = userService.registerUser(new User(username, password_hash, salt));
        
        if(result) {
            // user successfully registered, add info to session state
            
            User user = userService.findByUsername(username);
            sessionManager.getSessionState(id).login(user);
            
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "User with name " + username + " already exists";
        }
        
        
        return "/homepage";
    }
    
}
