package net.wintersjames.gameserver.Games.Chess;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.wintersjames.gameserver.Games.Chess.ChessPieces.Piece;
import net.wintersjames.gameserver.Games.GameMatch;
import net.wintersjames.gameserver.Games.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author james
 */
public class ChessMatch extends GameMatch {
    
	Logger logger = LoggerFactory.getLogger(ChessMatch.class);
	
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
			
		logger.info("moving {} -> {}", fromPos, toPos);
		
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
		
		// check if the next player has a legal move
		if(!state.hasLegalMove()) {
			logger.info("{} player has no legal move", state.isWhiteToMove() ? "white" : "black");
			if(state.isInCheck(state.isWhiteToMove() ? Piece.Color.WHITE : Piece.Color.BLACK)) {
				// checkmate
				state.setStatus(GameState.Status.WINNER_DECIDED);
				state.setWinner(state.isWhiteToMove() ? this.blackPlayer : this.whitePlayer);
			} else  {
				// stalemate
				state.setStatus(GameState.Status.DRAW);
			}
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
