package net.wintersjames.gameserver.Games.Chess.ChessPieces;

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
	public boolean canMove(int x, int y) {
		return false;
	}

	@Override
	public void move(int x, int y) {

	}
}
