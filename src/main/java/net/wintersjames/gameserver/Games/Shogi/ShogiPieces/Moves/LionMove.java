package net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves;

import java.util.List;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Piece;
import net.wintersjames.gameserver.Games.Shogi.ShogiState;

/**
 *
 * @author james
 */
public class LionMove extends MoveType {

	MoveType firstMove;
	List<MoveType> secondMoves;
	
	public LionMove(MoveType firstMove, List<MoveType> secondMoves) {
		super(firstMove.x, firstMove.y);
		this.firstMove = firstMove;
		this.secondMoves = secondMoves;
	}

	@Override
	public boolean isMoveLegal(Piece piece, int x, int y, ShogiState state) {
		return this.firstMove.isMoveLegal(piece, x, y, state);
	}

	@Override
	public boolean hasLegalMove(Piece piece, ShogiState state) {
		return this.firstMove.hasLegalMove(piece, state);
	}

	public List<MoveType> getSecondMoves() {
		return secondMoves;
	}
}
