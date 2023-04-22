/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games;

import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author james
 */
public abstract class GameMatch implements Serializable {
    
    private List<Integer> players;
	private List<Integer> currentPlayers;
    private List<ChatMessage> messages;
    private Class game;
	protected GameState gameState;
    final private long id;
	
	public enum HandleMoveResult {
		SUCCESS,
		FAIL,
		NEEDS_MORE_INFO
	}

    public GameMatch(long id, Class game, GameState gameState) {
        this.id = id;
        this.players = new ArrayList<>();
		this.currentPlayers = new ArrayList<>();
        this.messages = new ArrayList<>();
		this.gameState = gameState;
        this.game = game;
    }
    
    public void addPlayer(int uid) {
        if(!players.contains(uid)) {
            players.add(uid);
			currentPlayers.add(uid);
        }
    }
    
    public boolean containsPlayer(int uid) {
        return currentPlayers.contains(uid);
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

	public GameState getGameState(int uid) {
		return gameState;
	}

	public abstract HandleMoveResult handleMove(int uid, HttpServletRequest request);
	
	public Map<String, String> getAttributes(int uid) {
		return null;
	}
    
	public void resign(int uid) {
		currentPlayers.remove(currentPlayers.indexOf(uid));
		
		// remove user uid from the current players list
		// and decide the winner if there's only one player left
		if(currentPlayers.size() == 1) {
			gameState.setStatus(GameState.Status.WINNER_DECIDED);
			gameState.setWinner(currentPlayers.get(0));
		}
	}
}
