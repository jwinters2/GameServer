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
			
			// some pieces can promote on the final rank regardless of how they get there
			if(toY == boardWidth - 1 && toPromote.getCanPromoteOnFinalRank()) {
				return true;
			}
			
			if( toY < boardWidth - promotionWidth) {
				logger.info("y ({} & {}) >= {}", fromY, toY, boardWidth - promotionWidth);
				return false;
			}
			
			// piece must either start from outside the promotion zone, or capture a piece
			if(toCapture == null && fromY >= boardWidth - promotionWidth) {
				return false;
			}
			
		} else {
			
			// some pieces can promote on the final rank regardless of how they get there
			if(toY == 0 && toPromote.getCanPromoteOnFinalRank()) {
				return true;
			}
			
			if(toY >= promotionWidth) {
				logger.info("y ({} & {}) <= {}", fromY, toY, promotionWidth);
				return false;
			}
			
			// piece must either start from outside the promotion zone, or capture a piece
			if(toCapture == null && fromY < promotionWidth) {
				return false;
			}
		}
				
		return true;
	}
	
	@Override
	public boolean isPromotionMandatory(int toX, int toY, Piece piece, Piece.Color color) {
		return false;
	}
	
	@Override
	public boolean isPromotionMandatory(int toX, int toY, String pieceType, Piece.Color color) {
		return false;
	}
}
