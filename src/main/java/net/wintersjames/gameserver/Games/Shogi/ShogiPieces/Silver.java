package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.JumpMove;

/**
 *
 * @author james
 */
public class Silver extends Piece {

	public Silver(int x, int y, Color color) {
		super(x, y, color, "silver");
		
		this.moveSet.add(new JumpMove(-1,  1));
		this.moveSet.add(new JumpMove( 0,  1));
		this.moveSet.add(new JumpMove( 1,  1));
		this.moveSet.add(new JumpMove(-1,-1));
		this.moveSet.add(new JumpMove( 1,-1));
		
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
		return (this.isPromoted ? "+" : " ") + (this.color == Piece.Color.BLACK ? "S" : "s");
	}

	@Override
	public Piece deepCopy() {
		Piece retval = new Silver(this.x, this.y, this.color);
		retval.isPromoted = this.isPromoted;
		return retval;
	}
	
	@Override
	public String getPromotesTo() {
		return "gold";
	}
	
}
