package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import net.wintersjames.gameserver.Games.GameState;
import net.wintersjames.gameserver.Games.Shogi.ShogiState;

/**
 *
 * @author james
 */
public class JumpMove extends MoveType {

	public JumpMove(int x, int y) {
		super(x, y);
	}

	@Override
	public boolean isMoveLegal(Piece piece, int x, int y, ShogiState state) {
		int direction = piece.getColor() == Piece.Color.WHITE ? 1 : -1;
		return !(piece.getX() + this.x != x || piece.getY() + (this.y * direction) != y);
	}

	@Override
	public boolean hasLegalMove(Piece piece, ShogiState state) {
		return true;
	}
	
}
