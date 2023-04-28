package net.wintersjames.gameserver.Games.Shogi.Variants;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.MoveType;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Piece;
import net.wintersjames.gameserver.Games.Shogi.ShogiState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author james
 */
public class ChuShogiState extends ShogiState {
	
	Logger logger = LoggerFactory.getLogger(ChuShogiState.class);
	
	public ChuShogiState() {
		super(12, 4, "chushogi.json");
	}
	
	@Override
	public boolean canDrop(int toX, int toY, String pieceType, boolean isWhite) {
		return false;
	}
	
	@Override
	public boolean isPromotionOptional(int fromX, int fromY, int toX, int toY, Piece toCapture) {
		
		Piece toPromote = getPieceAt(toX, toY);
		
		// null pieces, already-promoted pieces, gold generals and kings can't promote
		if(toPromote == null 
			|| toPromote.getIsPromoted() 
			|| !toPromote.getCanPromote()) {
			logger.info("promotion not allowed here, isPromoted={}, type={}",
				toPromote == null ? null : toPromote.getIsPromoted(),
				toPromote == null ? null : toPromote.getType()
			);
			return false;
		}
		
		// piece must end in promotion zone
		if(toPromote.getColor() == Piece.Color.WHITE) {
			if( toY < boardWidth - promotionWidth) {
				logger.info("y ({} & {}) >= {}", fromY, toY, boardWidth - promotionWidth);
				return false;
			}
			
			// piece must either start from outside the promotion zone, or capture a piece
			if(toCapture == null && fromY >= boardWidth - promotionWidth) {
				return false;
			}
			
		} else {
			if(toY >= promotionWidth) {
				logger.info("y ({} & {}) <= {}", fromY, toY, promotionWidth);
				return false;
			}
			
			// piece must either start from outside the promotion zone, or capture a piece
			if(toCapture == null && fromY < promotionWidth) {
				return false;
			}
		}
		
		if(isPromotionMandatory(toX, toY, toPromote, toPromote.getColor())) {
			logger.info("promotion is mandatory");
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean isPromotionMandatory(int toX, int toY, Piece piece, Piece.Color color) {
		
		int spaceBeforeEdge = (color == Piece.Color.WHITE ? boardWidth - 1 - toY : toY);
		
		for(MoveType move: piece.getMoveSet() ) {
			
			// if we have space for another move, we don't need to promote
			if(move.getY() <= spaceBeforeEdge) {
				return false;
			}
		}
		
		return true;
	}
}
