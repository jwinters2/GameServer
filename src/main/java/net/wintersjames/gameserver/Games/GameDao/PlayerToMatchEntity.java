package net.wintersjames.gameserver.Games.GameDao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 *
 * @author james
 */
@Entity
@IdClass(PlayerToMatchId.class)
@Table(name = "player_to_matches")
@NamedQueries ({
	@NamedQuery(name = "PlayerToMatchEntity.findByPlayerId", query = "SELECT e from PlayerToMatchEntity e where e.playerId = :playerId")})
public class PlayerToMatchEntity {
	
	@Id
	@Column(name = "playerid")
	private int playerId;
	
	@Id
	@Column(name = "matchid")
	private long matchId;
	
	public PlayerToMatchEntity() {
	}

	public PlayerToMatchEntity(int playerid, long matchid) {
		this.playerId = playerid;
		this.matchId = matchid;
	}

	public long getMatchId() {
		return matchId;
	}

	public void setMatchId(long matchId) {
		this.matchId = matchId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	
}
