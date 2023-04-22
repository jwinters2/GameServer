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
	public HandleMoveResult handleMove(int uid, HttpServletRequest request) {
		
		String promotionStr = request.getParameter("promotion");
		if(promotionStr != null) {
			promotionStr = URLDecoder.decode(promotionStr, StandardCharsets.UTF_8);
		}
		
		
		
		String isDropStr = request.getParameter("drop");
		if(isDropStr != null) {
			isDropStr = URLDecoder.decode(isDropStr, StandardCharsets.UTF_8);
		}
		boolean isDrop = isDropStr != null && isDropStr.toLowerCase().equals("true");
		

		
		// get the color of the moving player
		boolean isWhite = (uid == whitePlayer);

		ShogiState state = (ShogiState)this.gameState;
		
		// if this move is a promotion, just promote the piece
		if(promotionStr != null) {
			if (state.isWhiteToMove() == isWhite && state.getPendingPromotion() != null) {
				 if(promotionStr.equalsIgnoreCase("true")) {
					 state.getPendingPromotion().promote();
				 }
				 
				 state.resetPendingPromotion();
				 state.nextMove();
				 return HandleMoveResult.SUCCESS;
			}
		}
		
		String toPos = request.getParameter("to");
		if(toPos != null) {
			toPos = URLDecoder.decode(toPos, StandardCharsets.UTF_8);
		} else {
			return HandleMoveResult.FAIL;
		}
		
		int toX = Integer.parseInt(toPos.split(",")[0]);
		int toY = Integer.parseInt(toPos.split(",")[1]);
		
		if(isDrop) {
			// dropping a piece
			String pieceType = request.getParameter("type");
			if(pieceType != null) {
				pieceType = URLDecoder.decode(pieceType, StandardCharsets.UTF_8);
			} else {
				return HandleMoveResult.FAIL;
			}
			
			if(!state.canDrop(toX, toY, pieceType, isWhite)) {
				return HandleMoveResult.FAIL;
			}
			
			state.drop(toX, toY, pieceType, isWhite);
			state.setSquaresToHighlight( toX, toY);
			
		} else {
			// moving a piece
			// if we're not dropping a piece, we need the from coords as well
			String fromPos = request.getParameter("from");
			if(fromPos != null) {
				fromPos = URLDecoder.decode(fromPos, StandardCharsets.UTF_8);
			} else {
				return HandleMoveResult.FAIL;
			}
			
			int fromX = Integer.parseInt(fromPos.split(",")[0]);
			int fromY = Integer.parseInt(fromPos.split(",")[1]);
			
			logger.info("moving ({},{}) to ({},{})", fromX, fromY, toX, toY);
			
			// check if move is valid
			if(!state.canMove(fromX, fromY, toX, toY, isWhite)) {
				return HandleMoveResult.FAIL;
			}
			
			state.move(fromX, fromY, toX, toY);
			state.setSquaresToHighlight(fromX, fromY, toX, toY);
			
			// check for promotion
			logger.info("checking for promotion");
			if(state.isPromotionOptional(fromX, fromY, toX, toY)) {
				logger.info("promotion required");
				state.setPendingPromotion(toX, toY);
				
				return HandleMoveResult.NEEDS_MORE_INFO;
			}

		}
					
		state.nextMove();
		return HandleMoveResult.SUCCESS;
	}
	
	@Override
	public Map<String, String> getAttributes(int uid) {
		Map<String, String> retval = new HashMap<>();
		retval.put("playerColor", uid == whitePlayer ? "white" : "black");
		return retval;
	}
	
}
