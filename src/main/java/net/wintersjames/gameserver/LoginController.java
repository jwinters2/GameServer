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
	
	private String sha256sum(String... s) {
		try {
			
			String message = "";
			for(String string: s) {
				message += string;
			}

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(message.getBytes(StandardCharsets.UTF_8));

			StringBuilder retval = new StringBuilder(2*hash.length);
			for(byte b: hash) {
				String digit = Integer.toHexString(0xFF & b);
				if(digit.length() == 1) {
						retval.append("0");
				}
				retval.append(digit);
			}
			return retval.toString();

		} catch (NoSuchAlgorithmException e) {
			return "";
		}
	}
    
    @GetMapping("/login")
    public String homePage(Model model, HttpServletRequest request, HttpServletResponse response) {             
        return "login";
    }
    
    @PostMapping("/login")
    @ResponseBody
    public String register(Model model, HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        
        
        String id = CookieUtils.getSessionCookie(request, response);
        
        String username = URLDecoder.decode(
                request.getParameter("username"),  
                StandardCharsets.UTF_8);
        String password = URLDecoder.decode(
                request.getParameter("password"), 
                StandardCharsets.UTF_8);
        
        User user = userService.findByUsername(username);
        String hash = user.getPasswordHash();
        String salt = user.getSalt();

		String enteredHash = sha256sum(password, salt);
        
        if(enteredHash.toLowerCase().equals(hash.toLowerCase())) {
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
