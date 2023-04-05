/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Queue;

import java.util.ArrayList;
import java.util.List;
import net.wintersjames.gameserver.User.User;

/**
 *
 * @author james
 */
public class GameQueue {

    final private ArrayList<User> enqueuedUsers;
    final private Class game;

    
    public GameQueue() {
        this.enqueuedUsers = new ArrayList<>();
        this.game = null;
    }
    
    public GameQueue(Class game) {
        this.enqueuedUsers = new ArrayList<>();
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

    public void challengeUser(int from_uid, int to_uid) {
        User fromUser = findUser(from_uid);
        User toUser = findUser(to_uid);
        
        System.out.println("user " + fromUser.getUsername() + " challenges " + toUser.getUsername());
    }
    
}
