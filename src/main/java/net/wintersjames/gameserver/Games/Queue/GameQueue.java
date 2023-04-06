/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wintersjames.gameserver.User.User;

/**
 *
 * @author james
 */
public class GameQueue {

    final private ArrayList<User> enqueuedUsers;
    final private HashMap<Long, GameInvite> invites;
    final private Class game;

    
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
        User fromUser = findUser(from_uid);
        User toUser = findUser(to_uid);
        
        GameInvite invite = new GameInvite(from_uid, to_uid, this.game);
        invites.put(invite.getTimestamp(), invite);
        
        System.out.println("user " + fromUser.getUsername() + " challenges " + toUser.getUsername());
    }

    public Class getGame() {
        return game;
    }
    
    public void remove(User user) {
        enqueuedUsers.remove(user);
    }
    
    public boolean startGame(GameInvite invite) {
        
        // if the invite or either party is missing, we can't start the game
        if(!invites.containsKey(invite.getTimestamp())
           || findUser(invite.getFromUid()) == null
           || findUser(invite.getToUid()) == null) {
            return false;
        }
        
        ArrayList<User> tempList = new ArrayList<>(enqueuedUsers);
        for(User user: tempList) {
            if(user.getUid() == invite.getFromUid() || user.getUid() == invite.getToUid()) {
                enqueuedUsers.remove(user);
            }
        }
        invites.remove(invite.getTimestamp());
        
        System.out.println("starting game");
        System.out.println(enqueuedUsers);
        System.out.println(invites);
        
        return true;
    }

    public void removeInvite(long timestamp) {
        invites.remove(timestamp);
    }
    
}
