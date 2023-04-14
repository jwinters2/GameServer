/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
	
	private String newSalt () {
        String salt = "";
        for(int i=0; i<20; i++) {
            salt += charset.charAt((int) Math.floor(Math.random() * charset.length()));
        }
        
        return salt;
    }
    
    @PostMapping("/register")
    @ResponseBody
    public String register(Model model, HttpServletRequest request, HttpServletResponse response) {
        
        
        String id = CookieUtils.getSessionCookie(request, response);
        
		String username = request.getParameter("username");
        String password = request.getParameter("password");
		
		if(username != null) {
			username = URLDecoder.decode(username, StandardCharsets.UTF_8);
		}
		if(password != null) {
			password = URLDecoder.decode(password, StandardCharsets.UTF_8);
		}
				
        String salt = newSalt();
        String password_hash = StringUtils.sha256sum(password, salt);

        boolean result = userService.registerUser(new User(username, password_hash, salt));
        
        if(result) {
            // user successfully registered, add info to session state
            
            User user = userService.findByUsername(username);
            sessionManager.getSessionState(id).login(user);
            
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "User with name \"" + username + "\" already exists";
        }
        
        
        return "/homepage";
    }
    
}
