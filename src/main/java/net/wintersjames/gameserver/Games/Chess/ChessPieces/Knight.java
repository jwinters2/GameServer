package net.wintersjames.gameserver.Games.Chess.ChessPieces;

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
	public boolean canMove(int x, int y) {
		return false;
	}

	@Override
	public void move(int x, int y) {

	}
}
