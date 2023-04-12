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
		return (Math.abs(this.x - x) <= 1 && Math.abs(this.y - y) <= 1);
	}

	@Override
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
		this.hasMoved = true;
	}
}
