package net.wintersjames.gameserver.Games.Chess.ChessPieces;

import net.wintersjames.gameserver.Games.Chess.ChessState;

/**
 *
 * @author james
 */
public class King extends Piece {
	
	public King(int x, int y, Color color) {
		super(x, y, color, "king");
	}

	@Override
	public char toChar() {
		return (color == Piece.Color.WHITE ? '\u265A' : '\u2654');
	}

	@Override
	public boolean canMove(int x, int y, ChessState state) {
		
		// castle kingside
		if(this.y == y && this.x + 2 == x ) {
			
			if(this.hasMoved) {
				return false;
			}
			
			Piece kingsideRook = state.getPieceAt(this.x + 3, this.y);
			// there must be a rook at this specific position, and it must not have moved
			if(kingsideRook == null || !(kingsideRook instanceof Rook) || kingsideRook.getHasMoved()) {
				return false;
			}
			// all squares inbetwen the king and rook must be empty
			if( state.getPieceAt(this.x + 1, this.y) != null ||
				state.getPieceAt(this.x + 2, this.y) != null 
				) {
				return false;
			}
			
			Piece.Color attackingColor = this.getColor() == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
			if(	state.isSquareUnderAttack(this.x, this.y, attackingColor) ||
				state.isSquareUnderAttack(this.x + 1, this.y, attackingColor) ||
				state.isSquareUnderAttack(this.x + 2, this.y, attackingColor)
					) {
				return false;
			}
			
			return true;
		}
		
		// castle queenside
		if(this.y == y && this.x - 2 == x ) {
			
			if(this.hasMoved) {
				return false;
			}
			
			Piece queensideRook = state.getPieceAt(this.x - 4, this.y);
			// there must be a rook at this specific position, and it must not have moved
			if(queensideRook == null || !(queensideRook instanceof Rook) || queensideRook.getHasMoved()) {
				return false;
			}
			// all squares inbetwen the king and rook must be empty
			if( state.getPieceAt(this.x - 1, this.y) != null ||
				state.getPieceAt(this.x - 2, this.y) != null ||
				state.getPieceAt(this.x - 3, this.y) != null 
				) {
				return false;
			}
			
			Piece.Color attackingColor = this.getColor() == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
			if(	state.isSquareUnderAttack(this.x, this.y, attackingColor) ||
				state.isSquareUnderAttack(this.x - 1, this.y, attackingColor) ||
				state.isSquareUnderAttack(this.x - 2, this.y, attackingColor)
					) {
				return false;
			}
			
			return true;
		}
		
		return (Math.abs(this.x - x) <= 1 && Math.abs(this.y - y) <= 1);
	}
	
	@Override public boolean hasLegalMove(ChessState state) {
		boolean retval = false;
		
		for(int dx = -1; dx <= 1; dx++) {
			for(int dy = -1; dy <= 1; dy++) {
				if(dx != 0 || dy != 0) {
					retval |= state.canMove(x, y, x+dx, y+dy, color == Piece.Color.WHITE);
				}
			}
		}
		
		return retval;
	}

	@Override
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
		this.hasMoved = true;
	}
	
	@Override
	public King deepCopy() {
		return new King(this.x, this.y, this.color);
	}
}
