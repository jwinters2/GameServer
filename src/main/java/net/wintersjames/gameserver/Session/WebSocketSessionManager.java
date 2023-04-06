/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Session;

import jakarta.inject.Singleton;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

/**
 *
 * @author james
 */
@Component
@Singleton
@Controller
@EnableAsync
public class WebSocketSessionManager {
    
    HashMap<Integer, Long> users;
    private long timeout = 30000; // 30 seconds
    
    Set<ListenToDisconnects> listeners;
    
    public WebSocketSessionManager() {
        users = new HashMap<>();
        listeners = new HashSet<>();
    }
    
    private long currentTimestamp() {
        return Instant.now().toEpochMilli();
    }
    
    private void updateListeners(int uid) {
        Iterator<ListenToDisconnects> it = listeners.iterator();
        while(it.hasNext()) {
            it.next().handleDisconnects(uid);
        }
    }
    
    @Async
    @Scheduled(fixedRate = 10000)
    public void removeInactiveUsers() {
        long timestamp = currentTimestamp();
        
        if(!users.isEmpty()) {
            Set<Integer> keys = users.keySet();
            for(int uid: keys) {
                if(timestamp - users.get(uid) > timeout) {
                    users.remove(uid);
                    updateListeners(uid);
                }
            }            
        }
    }
    
    @MessageMapping("/heart/{uid}")
    public void handleWebsocketMessage(@DestinationVariable("uid") int uid) {                
        System.out.println("heartbeat");
        users.put(uid, currentTimestamp());
    }
    
    public boolean isActive(int uid) {
        return users.containsKey(uid);
    }
    
    public void registerListener(ListenToDisconnects listener) {
        listeners.add(listener);
    }
}
