package net.wintersjames.gameserver.Games.Go;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.wintersjames.gameserver.Games.GameMatch;
import net.wintersjames.gameserver.Games.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author james
 */
public class GoMatch extends GameMatch {

	Logger logger = LoggerFactory.getLogger(GoMatch.class);
	
	final private int whitePlayer;
	final private int blackPlayer;
	
	public GoMatch(long id, int blackPlayer, int whitePlayer) {
        super(id, Go.class, new GoState());
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
    }

	@Override
	public GameMatch.HandleMoveResult handleMove(int uid, HttpServletRequest request) {
		
		String toPos = request.getParameter("to");
		if(toPos != null) {
			toPos = URLDecoder.decode(toPos, StandardCharsets.UTF_8);
		} else {
			return HandleMoveResult.FAIL;
		}
		
		logger.info("placing stone at ({})", toPos);
		int x = Integer.parseInt(toPos.split(",")[0]);
		int y = Integer.parseInt(toPos.split(",")[1]);
		
		Stone.Color movingColor = (uid == this.whitePlayer ? Stone.Color.WHITE : Stone.Color.BLACK);
		
		GoState state = (GoState)this.gameState;
		if(!state.canPlaceStone(x, y, movingColor)) {
			logger.info("cannot place stone");
			return HandleMoveResult.FAIL;
		}
		
		GoState nextState = new GoState(state);
		nextState.removeIfLastLibertyTaken(x, y, movingColor);
		nextState.placeStone(x, y, movingColor);
		logger.info("state hash: {}", nextState.boardStateHash());
		if(state.containsPreviousState(nextState)) {
			// ko rule, we're not allowed to repeat a position
			return HandleMoveResult.FAIL;
		}
		
		this.gameState = nextState;
		
		nextState.calculateLiberties();
		nextState.nextMove();
		
		nextState.calculateTerritory();
		
		return HandleMoveResult.SUCCESS;
	}
	
	@Override
	public Map<String, String> getAttributes(int uid) {
		Map<String, String> retval = new HashMap<>();
		retval.put("playerColor", uid == whitePlayer ? "white" : "black");
		return retval;
	}
	
}
