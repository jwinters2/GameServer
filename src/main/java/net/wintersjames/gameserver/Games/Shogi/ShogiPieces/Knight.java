package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.JumpMove;

/**
 *
 * @author james
 */
public class Knight extends Piece {
	public Knight(int x, int y, Piece.Color color) {
		super(x, y, color, "knight");
		
		this.moveSet.add(new JumpMove(-1, 2));
		this.moveSet.add(new JumpMove( 1, 2));
		
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
		return (this.isPromoted ? "+" : " ") + (this.color == Piece.Color.BLACK ? "N" : "n");
	}

	@Override
	public Piece deepCopy() {
		Piece retval = new Knight(this.x, this.y, this.color);
		retval.isPromoted = this.isPromoted;
		return retval;
	}
	
	@Override
	public String getPromotesTo() {
		return "gold";
	}
}
