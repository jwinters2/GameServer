/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author james
 */
public abstract class GameMatch {
    
    private List<Integer> players;
    private List<ChatMessage> messages;
    private Class game;
	protected GameState gameState;
    final private long id;

    public GameMatch(long id, Class game, GameState gameState) {
        this.id = id;
        this.players = new ArrayList<>();
        this.messages = new ArrayList<>();
		this.gameState = gameState;
        this.game = game;
    }
    
    public void addPlayer(int uid) {
        if(!players.contains(uid)) {
            players.add(uid);
        }
    }
    
    public boolean containsPlayer(int uid) {
        return players.contains(uid);
    }

    public Class getGame() {
        return game;
    }

    public void setGame(Class game) {
        this.game = game;
    }

    public long getId() {
        return id;
    }
    
    public void newMessage(int uid, String message) {
        messages.add(new ChatMessage(uid, message));
    }
    
    public List<Integer> getPlayers() {
        return new ArrayList(players);
    }
    
    public List<ChatMessage> getChatLog() {
        return new ArrayList(messages);
    }

	public GameState getGameState() {
		return gameState;
	}

	public abstract boolean handleMove(int uid, HttpServletRequest request);
    
}
