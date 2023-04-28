package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.JumpMove;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.LineMove;

/**
 *
 * @author james
 */
public class Lance extends Piece {

	public Lance(int x, int y, Color color) {
		super(x, y, color, "lance");
		
		this.moveSet.add(new LineMove( 0,  1));
		
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
		return " " + (this.color == Piece.Color.BLACK ? "L" : "l");
	}

	@Override
	public Piece deepCopy() {
		Piece retval = new Lance(this.x, this.y, this.color);
		retval.isPromoted = this.isPromoted;
		return retval;
	}
	
	@Override
	public String getPromotesTo() {
		return "gold";
	}
}