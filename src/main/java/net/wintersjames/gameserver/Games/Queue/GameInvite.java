/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Queue;

import java.io.Serializable;
import java.time.Instant;

/**
 *
 * @author james
 */
public class GameInvite implements Serializable {
    
    private long currentTimestamp() {
        return Instant.now().toEpochMilli();
    }
    
    private long timestamp;
    private int from_uid;
    private int to_uid;
    private Class game;

    public GameInvite(int from_uid, int to_uid, Class game) {
        this.timestamp = currentTimestamp();
        this.from_uid = from_uid;
        this.to_uid = to_uid;
        this.game = game;
    }

    public int getFromUid() {
        return from_uid;
    }

    public void setFromUid(int from_uid) {
        this.from_uid = from_uid;
    }

    public int getToUid() {
        return to_uid;
    }

    public void setToUid(int to_uid) {
        this.to_uid = to_uid;
    }

    public Class getGame() {
        return game;
    }

    public void setGame(Class game) {
        this.game = game;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
}
