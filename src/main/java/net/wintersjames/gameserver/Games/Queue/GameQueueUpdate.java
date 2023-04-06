/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Queue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import net.wintersjames.gameserver.User.User;

/**
 *
 * @author james
 */
public class GameQueueUpdate implements Serializable {
    private ArrayList<User> userList;
    private HashMap<Long, GameInvite> invites;
    
    public GameQueueUpdate(GameQueue gameQueue) {
        userList = new ArrayList<>();
        invites = new HashMap<>(gameQueue.getInvites());
        for(User user: gameQueue.getList()) {
            userList.add(user.clientSafe());
        }
    }
    
    public void cleanForUser(int uid) {
        removeUser(uid);
        filterInvites(uid);
    }
    
    private void removeUser(int uid) {
        
        int index = -1;
        
        for(User user: userList) {
            if(user.getUid() == uid) {
                index = userList.indexOf(user);
            }
        }
        
        if(index != -1) {
            userList.remove(index);                   
        }
    }
    
    private void filterInvites(int uid) {
        HashMap<Long, GameInvite> filteredInvites = new HashMap<>();
        for(GameInvite invite: invites.values()) {
            if(invite.getFromUid() == uid || invite.getToUid() == uid) {
                filteredInvites.put(invite.getTimestamp(), invite);
            }
        }
        invites = filteredInvites;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public HashMap<Long, GameInvite> getInvites() {
        return invites;
    }
    
}
