package net.wintersjames.gameserver.Games.GameDao;

import java.io.Serializable;

/**
 *
 * @author james
 */
public class PlayerToMatchId implements Serializable {
	private int playerId;
	private long matchId;
	
	public PlayerToMatchId() {
	}

	public PlayerToMatchId(int playerid, long matchid) {
		this.playerId = playerid;
		this.matchId = matchid;
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public long getMatchId() {
		return matchId;
	}

	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + this.playerId;
		hash = 67 * hash + (int) (this.matchId ^ (this.matchId >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final PlayerToMatchId other = (PlayerToMatchId) obj;
		if (this.playerId != other.playerId) {
			return false;
		}
		return this.matchId == other.matchId;
	}
	
	
}
