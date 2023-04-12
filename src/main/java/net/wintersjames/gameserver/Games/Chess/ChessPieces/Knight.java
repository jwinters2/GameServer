package net.wintersjames.gameserver.Games.Chess.ChessPieces;

import net.wintersjames.gameserver.Games.Chess.ChessState;

/**
 *
 * @author james
 */
public class Knight extends Piece {
	
	public Knight(int x, int y, Piece.Color color) {
		super(x, y, color, "knight");
	}

	@Override
	public char toChar() {
		return (color == Piece.Color.WHITE ? '\u265E' : '\u2658');
	}

	@Override
	public boolean canMove(int x, int y, ChessState state) {
		boolean retval = false;
		
		retval |= (Math.abs(this.x - x) == 2 && Math.abs(this.y - y) == 1);
		retval |= (Math.abs(this.x - x) == 1 && Math.abs(this.y - y) == 2);
		
		return retval;
	}

	@Override
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
		this.hasMoved = true;
	}
}
