package net.wintersjames.gameserver.Games;

/**
 *
 * @author james
 */
public abstract class GameState {
    public enum Result {
        PLAYER_1_WINS,
        PLAYER_2_WWINS,
        DRAW,
        INCOMPLETE,
    }
	
	final private String type;
	
	public GameState(String type) {
		this.type = type;
	}
	
	
	final public String getType() {
		return type;
	}
}
