package net.wintersjames.gameserver.Queue;

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
    
    public GameQueue enqueueUser(User user, Class game) {
        
        if(!queues.containsKey(game)) {
            queues.put(game, new GameQueue(game));
        }
        
        GameQueue queue = queues.get(game);
        queue.add(user);
        
        return queue;
    }

    public List<User> getQueue(Class game) {
        return queues.get(game).getList();
    }
    
}
