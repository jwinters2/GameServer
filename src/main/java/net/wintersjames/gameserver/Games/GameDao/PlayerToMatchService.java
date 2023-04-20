package net.wintersjames.gameserver.Games.GameDao;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author james
 */
@Service
public class PlayerToMatchService {
	
	Logger logger = LoggerFactory.getLogger(PlayerToMatchService.class);
	
	@Autowired
	private final PlayerToMatchRepository ptmRepository;
	
	public PlayerToMatchService(PlayerToMatchRepository ptmRepository) {
		this.ptmRepository = ptmRepository;
	}
	
	public List<Long> getMatches(int playerid) {
		
		List<PlayerToMatchEntity> ptms = ptmRepository.findAllByPlayerId(playerid);
		
		List<Long> retval = new ArrayList<>();
		for(PlayerToMatchEntity ptm: ptms) {
			retval.add(ptm.getMatchId());
		}
		return retval;
	}
	
	public boolean savePlayerToMatch(int playerid, long matchid) {
		try {
			PlayerToMatchEntity ptm = new PlayerToMatchEntity(playerid, matchid);
			ptmRepository.save(ptm);
			return true;
		} catch (Exception e) {
			logger.info("player to match entity failed to save (playerid={}, matchid={})", playerid, matchid);
			return false;
		}
	}
	
	public void deleteMatch(long matchid) {
		ptmRepository.deleteByMatchId(matchid);
	}
}
