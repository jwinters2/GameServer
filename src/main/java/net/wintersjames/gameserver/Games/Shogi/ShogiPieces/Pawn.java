package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import net.wintersjames.gameserver.Games.Chess.ChessState;

/**
 *
 * @author james
 */
public class Pawn extends Piece {

	public Pawn(int x, int y, Color color) {
		super(x, y, color, "pawn");
		
		this.moveSet.add(new JumpMove(0, 1));
		
		// promotes to a gold
		this.promotedMoveSet.add(new JumpMove(-1,  1));
		this.promotedMoveSet.add(new JumpMove( 0,  1));
		this.promotedMoveSet.add(new JumpMove( 1,  1));
		this.promotedMoveSet.add(new JumpMove(-1,  0));
		this.promotedMoveSet.add(new JumpMove( 1,  0));
		this.promotedMoveSet.add(new JumpMove( 0, -1));
	}

	@Override
	public String toChar() {
		return (this.isPromoted ? "+" : " ") + (this.color == Piece.Color.BLACK ? "P" : "p");
	}

	@Override
	public Piece deepCopy() {
		return new Pawn(this.x, this.y, this.color);
	}
	
}
