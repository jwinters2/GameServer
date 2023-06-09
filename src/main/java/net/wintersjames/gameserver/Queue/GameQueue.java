package net.wintersjames.gameserver.Queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wintersjames.gameserver.Games.GameMatchManager;
import net.wintersjames.gameserver.User.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

/**
 *
 * @author james
 */
public class GameQueue {

	Logger logger = LoggerFactory.getLogger(GameQueue.class);
    final private ArrayList<User> enqueuedUsers;
    final private HashMap<Long, GameInvite> invites;
    final private Class game;
    
    private static GameMatchManager matchManager;
    
    public GameQueue() {
        this.enqueuedUsers = new ArrayList<>();
        this.invites = new HashMap<>();
        this.game = null;
    }
    
    public GameQueue(Class game) {
        this.enqueuedUsers = new ArrayList<>();
        this.invites = new HashMap<>();
        this.game = game;
    }
        
    private User findUser(int uid) {
        for(User user: enqueuedUsers) {
            if(user.getUid() == uid) {
                return user;
            }
        }
        return null;
    }
    
    public void add(User user) {
        if(!enqueuedUsers.contains(user)) {
            enqueuedUsers.add(user);
        }
    }
    
    public List<User> getList() {
        return new ArrayList(enqueuedUsers);
    }

    public Map<Long, GameInvite> getInvites() {
        return invites;
    }
    
    public GameInvite getInvite(long timestamp) {
        return invites.get(timestamp);
    }

    public void challengeUser(int from_uid, int to_uid) {
		challengeUser(from_uid, to_uid, null);
	}
	
	public void challengeUser(int from_uid, int to_uid, Long continueMatchId) {
        User fromUser = findUser(from_uid);
        User toUser = findUser(to_uid);
        
        GameInvite invite = new GameInvite(from_uid, to_uid, this.game);
		if(continueMatchId != null) {
			invite.setTimestamp(continueMatchId);
		}
        invites.put(invite.getTimestamp(), invite);
        
        logger.info("user {} challenges {}", fromUser.getUsername(), toUser.getUsername());
    }

    public Class getGame() {
        return game;
    }
    
    public void remove(User user) {
        enqueuedUsers.remove(user);
    }
    
    public boolean startGame(GameInvite invite, GameMatchManager matchManager) {
        
        // if the invite or either party is missing, we can't start the game
        if(!invites.containsKey(invite.getTimestamp())
           || findUser(invite.getFromUid()) == null
           || findUser(invite.getToUid()) == null) {
			
			logger.info("failed to start game");
			logger.info(invites.containsKey(invite.getTimestamp()) ? "" : "invites do not contain invite");
			logger.info(findUser(invite.getFromUid()) == null ? "from uid cannot be found" : "");
			logger.info(findUser(invite.getToUid()) == null ? "from uid cannot be found" : "");

            return false;
        }
        
        ArrayList<User> tempList = new ArrayList<>(enqueuedUsers);
        for(User user: tempList) {
            if(user.getUid() == invite.getFromUid() || user.getUid() == invite.getToUid()) {
                enqueuedUsers.remove(user);
            }
        }
        invites.remove(invite.getTimestamp());
        
        logger.info("starting game");
        logger.info("enqueuedUsers: {}",enqueuedUsers);
        logger.info("invites: {}", invites);
        
        matchManager.newMatch(invite);
        
        return true;
    }

    public void removeInvite(long timestamp) {
        invites.remove(timestamp);
    }
    
}
