/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Session;

import java.util.HashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

/**
 *
 * @author james
 */
@Component
@SessionScope
public class SessionStateManager {
    
    final private HashMap<String, SessionState> cookies;

    public SessionStateManager() {
        // TODO: read from DB
        cookies = new HashMap<>();
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
