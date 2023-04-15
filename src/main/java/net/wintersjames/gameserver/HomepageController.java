/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import net.wintersjames.gameserver.Session.LoginState;
import net.wintersjames.gameserver.Session.SessionState;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.Games.Chess.Chess;
import net.wintersjames.gameserver.Games.Game;
import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author james
 */
@Controller
public class HomepageController {
    
	Logger logger = LoggerFactory.getLogger(HomepageController.class);
	
    @Autowired
    private SessionStateManager sessionManager;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String redirectHome(RedirectAttributes attributes,  HttpServletRequest request, HttpServletResponse response) {
        
        String id = CookieUtils.getSessionCookie(request, response);
        int uid = sessionManager.getSessionState(id).getLoginState().getUid();
        try {
			if(uid == 0) {
				// no user logged in, redirect to login
				response.sendRedirect("/login");
				return "login";
			} else {
				// user is logged in, redirect to homepage
				response.sendRedirect("/homepage");
				return "homepage";
			}
		} catch (Exception e) {
			logger.error("\"/\" redirect failed");
		}
        return "homepage";
    }
    
    @GetMapping("/homepage")
    public String homepage(Model model, HttpServletRequest request, HttpServletResponse response) {
        
        String id = CookieUtils.getSessionCookie(request, response);
        SessionState state = sessionManager.getSessionState(id);
        String username = state.getLoginState().getUsername();
        
        // users at the homepage aren't in any queues or games
        state.setGameQueue(null);
        
        System.out.println("username: " + username);

        if(username == null) {
            // user is not logged in, so give them the login page
            return "login";
        }
        
        List<Game> games = new ArrayList<>();
        games.add(new Chess());       
        for(int i=0; i<8; i++) {
            games.add(new Game("Title " + Integer.toString(i), 
                    "https://placehold.co/" + Integer.toString(400 + (i * 10)), 
                    "placeholder description.  read me please i am words" ));
        }
        
        model.addAttribute("username", username);
        model.addAttribute("uid", state.getLoginState().getUid());
        model.addAttribute("games", games);
        return "homepage";
    }
    
    @GetMapping("/logout")
    public RedirectView logout(HttpServletRequest request, HttpServletResponse response) {
        
        String id = CookieUtils.getSessionCookie(request, response);
        SessionState state = sessionManager.getSessionState(id);
        LoginState loginState = state.getLoginState();
        
        // reset everything
        // TODO: make a function for this
        loginState.setUid(0);
        loginState.setUsername(null);
        state.setGameQueue(null);
        
        return new RedirectView("/login");
    }
}
