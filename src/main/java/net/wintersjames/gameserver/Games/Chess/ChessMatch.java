package net.wintersjames.gameserver.Games.Chess;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.wintersjames.gameserver.Games.GameMatch;

/**
 *
 * @author james
 */
public class ChessMatch extends GameMatch {
    
	final private int whitePlayer;
	final private int blackPlayer;
	
	private String lastFromPos;
	private String lastToPos;
	
    public ChessMatch(long id, int whitePlayer, int blackPlayer) {
        super(id, Chess.class, new ChessState());
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
		
		String fromPos = request.getParameter("from");
		if(fromPos != null) {
			fromPos = URLDecoder.decode(fromPos, StandardCharsets.UTF_8);
		}
		
		String toPos = request.getParameter("to");
		if(toPos != null) {
			toPos = URLDecoder.decode(toPos, StandardCharsets.UTF_8);
		}
		
		// requests to promote are typically given right after the from/to positions
		// so just use the last received positions
		String promotion = request.getParameter("promotion");
		if(promotion != null) {
			promotion = URLDecoder.decode(promotion, StandardCharsets.UTF_8);
			fromPos = lastFromPos;
			toPos = lastToPos;
		}
		
		System.out.println("moving " + fromPos + " -> " + toPos);
		
		// get the color of the moving player
		boolean isWhite = (uid == whitePlayer);
		
		ChessState state = (ChessState)this.gameState;
		
		// check if move is valid
		if(!state.canMove(fromPos, toPos, isWhite)) {
			return false;
		}
		
		// check if we need to promote
		if(state.needsPromotion(fromPos, toPos)) {
			boolean successfulPromotion = state.promote(fromPos, promotion);
			if(!successfulPromotion) {
				state.setPendingPromotionFrom(uid);
				
				this.lastFromPos = fromPos;
				this.lastToPos = toPos;
				
				return true;
			}
			
			// we successfully promoted a piece, reset the pending promotion stuff
			state.setPendingPromotionFrom(null);
			this.lastFromPos = null;
			this.lastToPos = null;
		}
		
		state.resetEnPassant();
		
		state.captureAt(toPos);
		state.move(fromPos, toPos);
		
		state.nextMove();
		
		return true;
	}
    
	@Override
	public Map<String, String> getAttributes(int uid) {
		Map<String, String> retval = new HashMap<>();
		retval.put("playerColor", uid == whitePlayer ? "white" : "black");
		return retval;
	}
}
