/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Queue;

import java.io.Serializable;
import java.util.ArrayList;
import net.wintersjames.gameserver.User.User;

/**
 *
 * @author james
 */
public class GameQueueUpdate implements Serializable {
    private ArrayList<User> userList;
    
    public GameQueueUpdate(GameQueue gameQueue, int uidToIgnore) {
        userList = new ArrayList<>();
        for (User user: gameQueue.getList()) {
            if(user.getUid() != uidToIgnore) {
                userList.add(user.clientSafe());
            }
        }
    }

    public ArrayList<User> getUserList() {
        return userList;
    }
    
    
}
