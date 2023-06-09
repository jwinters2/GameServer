package net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves;

import net.wintersjames.gameserver.Games.GameState;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Piece;
import net.wintersjames.gameserver.Games.Shogi.ShogiState;

/**
 *
 * @author james
 */
public abstract class MoveType {
	
	final int x;
	final int y;
	
	public MoveType(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public abstract boolean isMoveLegal(Piece piece, int x, int y, ShogiState state);
	public abstract boolean hasLegalMove(Piece piece, ShogiState state);
	
	final protected int getDirection(Piece piece) {
		return piece.getColor() == Piece.Color.WHITE ? 1 : -1;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
}
