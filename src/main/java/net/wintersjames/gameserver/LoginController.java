/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import net.wintersjames.gameserver.Session.SessionStateManager;

import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
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
@PropertySource(value = "classpath:webpath.properties")
public class LoginController {
	
	Logger logger = LoggerFactory.getLogger(LoginController.class);
    
	@Value("${context-root}")
	private String contextRoot;
	
    @Autowired
    private SessionStateManager sessionManager;
    
    @Autowired
    UserService userService;
    
    @GetMapping("/login")
    public String homePage(Model model, HttpServletRequest request, HttpServletResponse response) {             
        model.addAttribute("contextRoot", contextRoot);
		return "login";
    }
    
    @PostMapping("/login")
    @ResponseBody
    public String login(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        
		model.addAttribute("contextRoot", contextRoot);
        
        String id = CookieUtils.getSessionCookie(request, response);
		logger.info("session id={}", id);
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
		
		if(username != null) {
			username = URLDecoder.decode(username, StandardCharsets.UTF_8);
		}
		if(password != null) {
			password = URLDecoder.decode(password, StandardCharsets.UTF_8);
		}
        
        User user = userService.findByUsername(username);
		if(user == null) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			return "No user named \"" + username + "\" found";
		}
		
        String hash = user.getPasswordHash();
        String salt = user.getSalt();

		String enteredHash = StringUtils.sha256sum(password, salt);
        
        if(enteredHash.toLowerCase().equals(hash.toLowerCase())) {
            // success, add user info to session state
            sessionManager.getSessionState(id).login(user);
            return "homepage";
        } else {
            // bad password
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return "Incorrect username/password";
        }
    }    
}
