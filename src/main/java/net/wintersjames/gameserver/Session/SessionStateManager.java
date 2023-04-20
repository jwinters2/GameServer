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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
	Logger logger = LoggerFactory.getLogger(SessionStateManager.class);
	
    final private HashMap<String, SessionState> cookies;
    final private HashMap<User, String> users;

    public SessionStateManager() {
        // TODO: read from DB
        cookies = new HashMap<>();
        users = new HashMap<>();
    }
    
    public SessionState getSessionState(String id) {
        if (cookies.containsKey(id)) {
			logger.info("state known for id {}, returning {}", id, cookies.get(id));
            return cookies.get(id);
        } else {
            SessionState retval = new SessionState();
            cookies.put(id, retval);
			logger.info("unknown state id {}, returning new session state", id);
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
	
	public boolean hasCookie(String id) {
		return cookies.containsKey(id);
	}
}
