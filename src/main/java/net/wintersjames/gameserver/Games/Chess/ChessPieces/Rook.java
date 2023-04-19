package net.wintersjames.gameserver.Games.Chess.ChessPieces;

import net.wintersjames.gameserver.Games.Chess.ChessState;

/**
 *
 * @author james
 */
public class Rook extends Piece {
	
	public Rook(int x, int y, Piece.Color color) {
		super(x, y, color, "rook");
	}

	@Override
	public char toChar() {
		return (color == Piece.Color.WHITE ? '\u265C' : '\u2656');
	}

	@Override
	public boolean canMove(int x, int y, ChessState state) {
		boolean retval = false;
		
		retval |= canMoveInLine(x, y,  1,  0, state);
		retval |= canMoveInLine(x, y, -1,  0, state);
		retval |= canMoveInLine(x, y,  0,  1, state);
		retval |= canMoveInLine(x, y,  0, -1, state);

		return retval;
	}

	@Override
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
		this.hasMoved = true;
	}
	
	@Override
	public Rook deepCopy() {
		return new Rook(this.x, this.y, this.color);
	}
}
