package net.wintersjames.gameserver.Games;

import java.io.Serializable;

/**
 *
 * @author james
 */
public abstract class GameState  implements Serializable {
    public enum Status {
        WINNER_DECIDED,
        ABANDONED,
        DRAW,
        INCOMPLETE,
    }
	
	
	final private String type;
	private Status status;
	private Integer winner;
	
	public GameState(String type) {
		this.type = type;
		this.status = Status.INCOMPLETE;
		this.winner = null;
	}
	
	final public String getType() {
		return type;
	}

	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}

	public Integer getWinner() {
		return winner;
	}

	public void setWinner(Integer winner) {
		this.winner = winner;
	}
}
