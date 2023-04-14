package net.wintersjames.gameserver.Games.GameDao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.util.List;

/**
 *
 * @author james
 */
@Entity
@Table(name = "matches")
@NamedQueries ({
	@NamedQuery(name = "GameMatchEntity.findByMatchid", query = "SELECT m from GameMatchEntity m where m.matchid = :matchid")})
public class GameMatchEntity {
	
	@Id
	@Column(name = "matchid")
	long matchid;
	
	@Column(name = "gamedata")
	String gamedata;
	
	@Column(name = "players")
	List<Integer> players;
	
	@Column(name = "game")
	String game;

	public GameMatchEntity() {
	}
	
	public GameMatchEntity(long matchid, String gamedata, List<Integer> players, String game) {
		this.matchid = matchid;
		this.gamedata = gamedata;
		this.players = players;
		this.game = game;
	}

	public long getMatchid() {
		return matchid;
	}

	public void setMatchid(long matchid) {
		this.matchid = matchid;
	}

	public String getGamedata() {
		return gamedata;
	}

	public List<Integer> getPlayers() {
		return players;
	}

	public void setPlayers(List<Integer> players) {
		this.players = players;
	}

	public void setGamedata(String gamedata) {
		this.gamedata = gamedata;
	}

	public String getGame() {
		return game;
	}

	public void setGame(String game) {
		this.game = game;
	}
	
	
	
}
