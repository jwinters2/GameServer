/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Queue;

import java.util.ArrayList;
import net.wintersjames.gameserver.User.User;

/**
 *
 * @author james
 */
public class GameQueueUpdate {
    private ArrayList<User> userList;
    
    public GameQueueUpdate(GameQueue gameQueue) {
        userList = new ArrayList<>();
        for (User user: gameQueue.getList()) {
            userList.add(user.clientSafe());
        }
    }
}
