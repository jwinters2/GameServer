package net.wintersjames.gameserver.Games.GameDao;

import java.util.List;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author james
 */
@Repository
@EntityScan("net.wintersjames.gameserver.Games.GameDao")
public interface PlayerToMatchRepository extends JpaRepository<PlayerToMatchEntity, PlayerToMatchId> {
	public List<PlayerToMatchEntity> findAllByPlayerId(int playerId);
}
