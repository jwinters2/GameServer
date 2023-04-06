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
public abstract class GameMatch {
    
    private class ChatMessage {
        public int uid;
        public String message;
        public long timestamp;

        public ChatMessage(int uid, String message) {
            this.uid = uid;
            this.message = message;
            this.timestamp = Instant.now().toEpochMilli();
        }
    }
}
