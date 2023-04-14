package net.wintersjames.gameserver.Games.GameDao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.List;
import net.wintersjames.gameserver.Games.GameMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author james
 */
@Service
public class GameMatchPersistenceService {
	
	@Autowired
	private final GameMatchRepository matchRepository;
	
	public GameMatchPersistenceService(GameMatchRepository matchRepository) {
		this.matchRepository = matchRepository;
	}
	
	public boolean saveMatch(GameMatch match) {
		try {
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ObjectOutputStream objStream = new ObjectOutputStream(byteArray);
			
			objStream.writeObject(match);
			objStream.close();
			
			String encodedMatch = Base64.getEncoder().encodeToString(byteArray.toByteArray());
			
			GameMatchEntity gme = new GameMatchEntity(
					match.getId(), 
					encodedMatch, 
					match.getPlayers(),
					match.getGame().getSimpleName());
			matchRepository.save(gme);
			return true;
			
		} catch (IOException e) {
			System.out.println("match (matchid=" + match.getId() + ") failed to serialize");
			e.printStackTrace();
			return false;
		}
	}
	
	public GameMatch getMatch(long matchid) {
		GameMatchEntity gme = matchRepository.findByMatchid(matchid);
		
		if(gme == null) {
			return null;
		}
		
		try {
			byte[] gamedataBytes = Base64.getDecoder().decode(gme.getGamedata());
			
			ByteArrayInputStream byteArray = new ByteArrayInputStream(gamedataBytes);
			ObjectInputStream objStream = new ObjectInputStream(byteArray);
			
			GameMatch match = (GameMatch) objStream.readObject();
			
			return match;
			
		} catch (IOException e) {
			System.out.println("match (matchid=" + matchid + ") failed to deserialize: IOException");
			return null;
		} catch (ClassNotFoundException e) {
			System.out.println("match (matchid=" + matchid + ") failed to deserialize: ClassNotFoundException");
			return null;
		}
	}
	
	public List<Integer> getPlayersFromMatchId(long matchid) {
		GameMatchEntity gme = matchRepository.findByMatchid(matchid);
		
		if(gme == null) {
			return null;
		}
		
		return gme.getPlayers();
	}
	
	public void deleteMatch(long matchid) {
		matchRepository.deleteById(matchid);
	}
}
