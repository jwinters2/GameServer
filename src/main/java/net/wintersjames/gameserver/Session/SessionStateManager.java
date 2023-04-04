/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Session;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.security.SecureRandom;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 *
 * @author james
 */
@Component
@SessionScope
public class SessionStateManager {
    
    private HashMap<String, SessionState> cookies;
    private SecureRandom random;

    public SessionStateManager() {
        // TODO: read from DB
        cookies = new HashMap<>();
        random = new SecureRandom();
    }
    
    public SessionState getSessionState(String id) {
        if (cookies.containsKey(id)) {
            return cookies.get(id);
        } else {
            SessionState retval = new SessionState();
            cookies.put(id, retval);
            return retval;
        }
    }
}
