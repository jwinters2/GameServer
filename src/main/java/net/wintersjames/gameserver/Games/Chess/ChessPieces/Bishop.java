package net.wintersjames.gameserver.Games.Chess.ChessPieces;

import net.wintersjames.gameserver.Games.Chess.ChessState;

/**
 *
 * @author james
 */
public class Bishop extends Piece {
	
	public Bishop(int x, int y, Piece.Color color) {
		super(x, y, color, "bishop");
	}

	@Override
	public char toChar() {
		return (color == Piece.Color.WHITE ? '\u265D' : '\u2657');
	}

	@Override
	public boolean canMove(int x, int y, ChessState state) {
		boolean retval = false;
		
		retval |= canMoveInLine(x, y,  1,  1, state);
		retval |= canMoveInLine(x, y,  1, -1, state);
		retval |= canMoveInLine(x, y, -1,  1, state);
		retval |= canMoveInLine(x, y, -1, -1, state);

		return retval;
	}
	
	@Override public boolean hasLegalMove(ChessState state) {
		boolean retval = false;
		
		retval |= hasLegalMoveInDirection( 1,  1, state);
		retval |= hasLegalMoveInDirection(-1,  1, state);
		retval |= hasLegalMoveInDirection( 1, -1, state);
		retval |= hasLegalMoveInDirection(-1, -1, state);
		
		return retval;
	}

	@Override
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
		this.hasMoved = true;
	}
	
	@Override
	public Bishop deepCopy() {
		return new Bishop(this.x, this.y, this.color);
	}
}
