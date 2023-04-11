package net.wintersjames.gameserver.Games.Chess.ChessPieces;

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
	public boolean canMove(int x, int y) {
		return false;
	}

	@Override
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
		this.hasMoved = true;
	}
}
