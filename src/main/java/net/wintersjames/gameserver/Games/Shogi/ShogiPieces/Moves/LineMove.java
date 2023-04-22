package net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Piece;
import net.wintersjames.gameserver.Games.Shogi.ShogiState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author james
 */
public class LineMove extends MoveType {
	
	Logger logger = LoggerFactory.getLogger(LineMove.class);

	public LineMove(int x, int y) {
		super(x, y);
	}

	@Override
	public boolean isMoveLegal(Piece piece, int x, int y, ShogiState state) {
		
		logger.info("checking linemove from ({},{}) to ({},{}) in direction ({},{})",
				piece.getX(), piece.getY(),
				x, y,
				this.x, this.y * getDirection(piece)
		);
		int xToCheck = piece.getX() + this.x;
		int yToCheck = piece.getY() + (this.y * getDirection(piece));
		
		while(xToCheck >= 0 && xToCheck < 9 && yToCheck >= 0 && yToCheck < 9) {
			
			logger.info("checking ({},{})", xToCheck, yToCheck);
			// we hit our target point
			if (xToCheck == x && yToCheck == y) {
				logger.info("success: target found");
				return true;
			}
			
			// if we hit a piece (we're not checking our final position yet), we're blocked
			if(state.getPieceAt(xToCheck, yToCheck) != null) {
				logger.info("fail: piece is blocked");
				return false;
			}
			
			xToCheck += this.x;
			yToCheck += (this.y * getDirection(piece));
		}
		
		logger.info("fail: hit edge of board");
		return false;
	}

	@Override
	public boolean hasLegalMove(Piece piece, ShogiState state) {
		
		int xToCheck = piece.getX() + this.x;
		int yToCheck = piece.getY() + this.y;
		
		while(xToCheck >= 0 && xToCheck < 9 && yToCheck >= 0 && yToCheck < 9) {
			
			// we hit our target point
			if (state.canMove(piece.getX(), piece.getY(), xToCheck, yToCheck, piece.getColor() == Piece.Color.WHITE)) {
				return true;
			}
			
			xToCheck += this.x;
			yToCheck += (this.y * getDirection(piece));
		}
		
		return false;
	}
	
}
