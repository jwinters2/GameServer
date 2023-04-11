package net.wintersjames.gameserver.Games.Chess.ChessPieces;

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
