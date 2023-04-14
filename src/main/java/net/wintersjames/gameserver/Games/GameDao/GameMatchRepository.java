package net.wintersjames.gameserver.Games.GameDao;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author james
 */
@Repository
@EntityScan("net.wintersjames.gameserver.Games.GameDao")
public interface GameMatchRepository extends JpaRepository<GameMatchEntity, Long> {
	public GameMatchEntity findByMatchid(long matchid);
}
