/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Session;

import jakarta.inject.Singleton;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import net.wintersjames.gameserver.User.User;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 *
 * @author james
 */
@Component
@Singleton
public class SessionStateManager {
    
    final private HashMap<String, SessionState> cookies;
    final private HashMap<User, String> users;

    public SessionStateManager() {
        // TODO: read from DB
        cookies = new HashMap<>();
        users = new HashMap<>();
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

    public SessionState getUserSession(User user) {
        return getUserSession(user.getUid());
    }
    
    public SessionState getUserSession(int uid) {
        Collection<SessionState> states = cookies.values();
        for(SessionState state: states) {
            if(state.getLoginState().getUid() == uid) {
                return state;
            }
        }
        return null;
    }
}
