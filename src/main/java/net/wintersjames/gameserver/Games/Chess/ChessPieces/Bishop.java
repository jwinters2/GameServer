package net.wintersjames.gameserver.Games.Chess.ChessPieces;

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
