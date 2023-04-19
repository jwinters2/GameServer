/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Session;

/**
 *
 * @author james
 */
public class LoginState {
    private String salt;
    private int uid;
    private String username;
	
	public LoginState() {
		this.salt = null;
		this.uid = 0;
		this.username = null;
	}
    
    public String getSalt() {
        return salt;
    }
    
    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getUid() {
        return this.uid;
    }
    
    public void setUid(int uid) {
        this.uid = uid;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }  
}
