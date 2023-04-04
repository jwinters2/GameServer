/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Queue;

import jakarta.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import net.wintersjames.gameserver.User.User;
import org.springframework.stereotype.Component;

/**
 *
 * @author james
 */
@Component
@Singleton
public class GameQueueManager {
    
    final private HashMap<Class, GameQueue> queues;
    
    public GameQueueManager() {
        queues = new HashMap<>();
    }
    
    public boolean enqueueUser(User user, Class game) {
        
        if(!queues.containsKey(game)) {
            queues.put(game, new GameQueue());
        }
        
        GameQueue queue = queues.get(game);
        queue.add(user);
        
        return true;
    }

    public List<User> getQueue(Class game) {
        return queues.get(game).getList();
    }
    
}
