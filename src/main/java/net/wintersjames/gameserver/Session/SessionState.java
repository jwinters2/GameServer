/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Session;

import net.wintersjames.gameserver.Games.GameMatch;
import net.wintersjames.gameserver.Queue.GameQueue;
import net.wintersjames.gameserver.User.User;

/**
 *
 * @author james
 */
public class SessionState {
    
    private LoginState loginState;
    private GameQueue gameQueue;
    private GameMatch gameMatch;
    
    public SessionState() {
        this.loginState = new LoginState();
    }
    
    public LoginState getLoginState() {
        return this.loginState;
    }

    public void login(User user) {
        loginState.setUid(user.getUid());
        loginState.setUsername(user.getUsername());
    }

    public GameQueue getGameQueue() {
        return gameQueue;
    }

    public void setGameQueue(GameQueue gameQueue) {
        this.gameQueue = gameQueue;
    }

    public GameMatch getGameMatch() {
        return gameMatch;
    }

    public void setGameMatch(GameMatch gameMatch) {
        this.gameMatch = gameMatch;
    }
}
