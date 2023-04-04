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
    
    public GameQueue() {
        enqueuedUsers = new ArrayList<>();
    }
    
    public void add(User user) {
        if(!enqueuedUsers.contains(user)) {
            enqueuedUsers.add(user);
        }
    }
    
    public List getList() {
        return new ArrayList(enqueuedUsers);
    }
    
}
