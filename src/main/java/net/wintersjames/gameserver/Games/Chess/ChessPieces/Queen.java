package net.wintersjames.gameserver.Games.Chess.ChessPieces;

import net.wintersjames.gameserver.Games.Chess.ChessState;

/**
 *
 * @author james
 */
public class Queen extends Piece {
	
	public Queen(int x, int y, Piece.Color color) {
		super(x, y, color, "queen");
	}

	@Override
	public char toChar() {
		return (color == Piece.Color.WHITE ? '\u265B' : '\u2655');
	}

	@Override
	public boolean canMove(int x, int y, ChessState state) {
		boolean retval = false;
		
		retval |= canMoveInLine(x, y,  1,  0, state);
		retval |= canMoveInLine(x, y, -1,  0, state);
		retval |= canMoveInLine(x, y,  0,  1, state);
		retval |= canMoveInLine(x, y,  0, -1, state);
		
		retval |= canMoveInLine(x, y,  1,  1, state);
		retval |= canMoveInLine(x, y,  1, -1, state);
		retval |= canMoveInLine(x, y, -1,  1, state);
		retval |= canMoveInLine(x, y, -1, -1, state);

		return retval;
	}
	
	@Override public boolean hasLegalMove(ChessState state) {
		boolean retval = false;
		
		retval |= hasLegalMoveInDirection(  0,  1, state);
		retval |= hasLegalMoveInDirection(  0, -1, state);
		retval |= hasLegalMoveInDirection(  1,  0, state);
		retval |= hasLegalMoveInDirection( -1,  0, state);
		
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
	public Queen deepCopy() {
		return new Queen(this.x, this.y, this.color);
	}
}
