package net.wintersjames.gameserver.Games;

import java.io.Serializable;

/**
 *
 * @author james
 */
public abstract class GameState  implements Serializable {
    public enum Status {
        PLAYER_1_WINS,
        PLAYER_2_WWINS,
        DRAW,
        INCOMPLETE,
    }
	
	final private String type;
	private Status status;
	
	public GameState(String type) {
		this.type = type;
		this.status = Status.INCOMPLETE;
	}
	
	final public String getType() {
		return type;
	}

	public Status getStatus() {
		return status;
	}
}
