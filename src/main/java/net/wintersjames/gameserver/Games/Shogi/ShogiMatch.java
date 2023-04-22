package net.wintersjames.gameserver.Games.Shogi;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.wintersjames.gameserver.Games.Chess.ChessState;
import net.wintersjames.gameserver.Games.GameMatch;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Piece;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author james
 */
public class ShogiMatch extends GameMatch {

	Logger logger = LoggerFactory.getLogger(ShogiMatch.class);
	
	final private int whitePlayer;
	final private int blackPlayer;
	
	private String lastFromPos;
	private String lastToPos;
	
	public ShogiMatch(long id, int whitePlayer, int blackPlayer) {
        super(id, Shogi.class, new ShogiState());
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
    }
	
	public enum MoveResult {
		SUCCESS,
		FAIL,
		NEEDS_PROMOTION
	}

	@Override
	public boolean handleMove(int uid, HttpServletRequest request) {
		
		String toPos = request.getParameter("to");
		if(toPos != null) {
			toPos = URLDecoder.decode(toPos, StandardCharsets.UTF_8);
		} else {
			return false;
		}
		
		String isDropStr = request.getParameter("drop");
		if(isDropStr != null) {
			isDropStr = URLDecoder.decode(isDropStr, StandardCharsets.UTF_8);
		}
		boolean isDrop = isDropStr != null && isDropStr.toLowerCase().equals("true");
		
		int toX = Integer.parseInt(toPos.split(",")[0]);
		int toY = Integer.parseInt(toPos.split(",")[1]);
		
		// get the color of the moving player
		boolean isWhite = (uid == whitePlayer);

		ShogiState state = (ShogiState)this.gameState;
		
		if(!isDrop) {
			// moving a piece
			// if we're not dropping a piece, we need the from coords as well
			String fromPos = request.getParameter("from");
			if(fromPos != null) {
				fromPos = URLDecoder.decode(fromPos, StandardCharsets.UTF_8);
			} else {
				return false;
			}
			
			int fromX = Integer.parseInt(fromPos.split(",")[0]);
			int fromY = Integer.parseInt(fromPos.split(",")[1]);
			
			logger.info("moving ({},{}) to ({},{})", fromX, fromY, toX, toY);
			
			// check if move is valid
			if(!state.canMove(fromX, fromY, toX, toY, isWhite)) {
				return false;
			}
			
			state.move(fromX, fromY, toX, toY);
			state.nextMove();
		}
		
		return true;
	}
	
	@Override
	public Map<String, String> getAttributes(int uid) {
		Map<String, String> retval = new HashMap<>();
		retval.put("playerColor", uid == whitePlayer ? "white" : "black");
		return retval;
	}
	
}
