/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.wintersjames.gameserver.Session.LoginState;
import net.wintersjames.gameserver.Session.SessionState;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.Games.Chess.Chess;
import net.wintersjames.gameserver.Games.Game;
import net.wintersjames.gameserver.Games.Shogi.Shogi;
import net.wintersjames.gameserver.User.User;
import net.wintersjames.gameserver.User.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 *
 * @author james
 */
@Controller
@PropertySource(value = "classpath:webpath.properties")
public class HomepageController {
    
	Logger logger = LoggerFactory.getLogger(HomepageController.class);
	
	@Value("${context-root}")
	private String contextRoot;
	
    @Autowired
    private SessionStateManager sessionManager;
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/")
    public String redirectHome(
			Model model, 
			RedirectAttributes attributes,  
			HttpServletRequest request, 
			HttpServletResponse response) {
        
		model.addAttribute("contextRoot", contextRoot);
		
        String id = CookieUtils.getSessionCookie(request, response, sessionManager);
        int uid = sessionManager.getSessionState(id).getLoginState().getUid();
        try {
			if(uid == 0) {
				// no user logged in, redirect to login
				response.sendRedirect(contextRoot + "/login");
				return "login";
			} else {
				// user is logged in, redirect to homepage
				response.sendRedirect(contextRoot + "/homepage");
				return "homepage";
			}
		} catch (IOException e) {
			logger.error("\"/\" redirect failed");
		}
        return "homepage";
    }
    
    @GetMapping("/homepage")
    public String homepage(Model model, HttpServletRequest request, HttpServletResponse response) {
		
		model.addAttribute("contextRoot", contextRoot);
        
        String id = CookieUtils.getSessionCookie(request, response, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
		if(HTTPUtils.redirectIfNotLoggedIn(state.getLoginState().getUid(), response, contextRoot + "/login")) {
			return "login";
		}
        String username = state.getLoginState().getUsername();
        
        // users at the homepage aren't in any queues or games
        state.setGameQueue(null);
        
        logger.info("username: {}", username);

        if(username == null) {
            // user is not logged in, so give them the login page
			try {
				response.sendRedirect(contextRoot + "/login");
			} catch (IOException e) {
				logger.error("\"/homepage\" redirect failed");
			}
            return "login";
        }
        
        List<Game> games = new ArrayList<>();
        games.add(new Chess());       
        games.add(new Shogi());
        
        model.addAttribute("username", username);
        model.addAttribute("uid", state.getLoginState().getUid());
        model.addAttribute("games", games);
        return "homepage";
    }
    
    @GetMapping("/logout")
    public String logout(
			Model model, 
			HttpServletRequest request, 
			HttpServletResponse response) {
        
		model.addAttribute("contextRoot", contextRoot);
		
        String id = CookieUtils.getSessionCookie(request, response, sessionManager);
        SessionState state = sessionManager.getSessionState(id);
        LoginState loginState = state.getLoginState();
        
        // reset everything
        // TODO: make a function for this
        loginState.setUid(0);
        loginState.setUsername(null);
        state.setGameQueue(null);
        
		try {
			response.sendRedirect(contextRoot + "/login");
		} catch (IOException e) {
			logger.error("logout redirect failed");
		}
        return "login";
    }
	
	@ExceptionHandler
	public String error(Model model, HttpServletResponse response, Exception exception) {
		model.addAttribute("contextRoot", contextRoot);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream, true, StandardCharsets.UTF_8);
		exception.printStackTrace(printStream);
		String stackTrace = outputStream.toString(StandardCharsets.UTF_8);
		
		model.addAttribute("error", exception.getMessage());
		model.addAttribute("errorDetails", stackTrace);
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return "error";
	}
}
