package net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.MoveType;
import net.wintersjames.gameserver.Games.GameState;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Piece;
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
		return (piece.getX() + this.x == x && piece.getY() + (this.y * getDirection(piece)) == y);
	}

	@Override
	public boolean hasLegalMove(Piece piece, ShogiState state) {
		return state.canMove(
			piece.getX(), 
			piece.getY(), 
			piece.getX() + this.x, 
			piece.getY() + (this.y * getDirection(piece)), 
			piece.getColor() == Piece.Color.WHITE);
	}
	
}
