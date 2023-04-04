/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Session;

import net.wintersjames.gameserver.User.User;

/**
 *
 * @author james
 */
public class SessionState {
    
    private LoginState loginState;
    
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
}
