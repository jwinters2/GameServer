/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import net.wintersjames.gameserver.Session.SessionState;
import net.wintersjames.gameserver.Session.SessionStateManager;
import net.wintersjames.gameserver.User.Games.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author james
 */
@Controller
public class HomepageController {
    
    @Autowired
    SessionStateManager sessionManager;
    
    @GetMapping("/homepage")
    public String homepage(Model model, HttpServletRequest request, HttpServletResponse response) {
        
        String id = CookieUtils.getSessionCookie(request, response);
        SessionState state = sessionManager.getSessionState(id);
        String username = state.getLoginState().getUsername();
        
        System.out.println("username: " + username);

        if(username == null) {
            // user is not logged in, so give them the login page
            return "login";
        }
        
        List<Game> games = new ArrayList<>();
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
}
