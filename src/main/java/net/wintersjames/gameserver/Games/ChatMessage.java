/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games;

import java.time.Instant;

/**
 *
 * @author james
 */
public class ChatMessage {
    private int uid;
    private String message;
    final private long timestamp;

    public ChatMessage(int uid, String message) {
        this.uid = uid;
        this.message = message;
        this.timestamp = Instant.now().toEpochMilli();
    }

    public int getUid() {
        return uid;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
    
}
