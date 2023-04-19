/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wintersjames.gameserver.Games.Chess.ChessMatch;
import net.wintersjames.gameserver.Games.GameDao.GameMatchPersistenceService;
import net.wintersjames.gameserver.Games.GameDao.PlayerToMatchService;
import net.wintersjames.gameserver.Games.Queue.GameInvite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author james
 */
@Component
public class GameMatchManager {
    
	Logger logger = LoggerFactory.getLogger(GameMatchManager.class);
	
	private Map<Long, GameMatch> matches;
	
	@Autowired
	private GameMatchPersistenceService matchPersistenceService;
	
	@Autowired
	private PlayerToMatchService ptmService;

    public GameMatchManager() {
        this.matches = new HashMap<>();
    }
    
    public GameMatch newMatch(GameInvite invite) {
        
		// find a previous incomplete game if there is one
        GameMatch newMatch = getMatch(invite.getFromUid(), invite.getTimestamp());
		
		// otherwise start a new one
        if(newMatch == null)
		{
			if(invite.getGameStr().toLowerCase().equals("chess")) {
				newMatch = new ChessMatch(invite.getTimestamp(), invite.getToUid(), invite.getFromUid());
			}
        }
        
        if(newMatch != null) {
            matches.put(invite.getTimestamp(), newMatch);
            newMatch.addPlayer(invite.getFromUid());
            newMatch.addPlayer(invite.getToUid());
        }
        
        logger.info("matches: {}", matches);

        return null;
    }
    
    public GameMatch getMatch(int uid, long matchid) {
        
        GameMatch match = matches.get(matchid);
        if(match != null && match.containsPlayer(uid)) {
            return match;
        }
		
		GameMatch savedMatch = matchPersistenceService.getMatch(matchid);
		if(savedMatch != null && savedMatch.containsPlayer(uid)) {
			if(savedMatch.getGameState(uid).getStatus() == GameState.Status.INCOMPLETE) {
				logger.info("found saved match {}",matchid);
				this.matches.put(matchid, savedMatch);
				return savedMatch;
			} else {
				logger.info("match {} has been completed", matchid);
			}
		}
        
        return null;
    }
	
	public List<GameMatch> getMatchesByUid(int uid) {
		
		List<GameMatch> retval = new ArrayList<>();
		for(GameMatch match: matches.values()) {
			if(match.containsPlayer(uid)) {
				retval.add(match);
			}
		}
		return retval;
	}
	
	public void removeMatch(long matchid) {
		matches.remove(matchid);
		ptmService.deleteMatch(matchid);
		matchPersistenceService.deleteMatch(matchid);
	}
}
