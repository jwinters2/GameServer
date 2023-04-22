package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.JumpMove;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.LineMove;

/**
 *
 * @author james
 */
public class Rook extends Piece {

	public Rook(int x, int y, Color color) {
		super(x, y, color, "rook");
		
		this.moveSet.add(new LineMove(-1,  0));
		this.moveSet.add(new LineMove( 1,  0));
		this.moveSet.add(new LineMove( 0, -1));
		this.moveSet.add(new LineMove( 0,  1));
		
		this.promotedMoveSet.add(new LineMove(-1,  0));
		this.promotedMoveSet.add(new LineMove( 1,  0));
		this.promotedMoveSet.add(new LineMove( 0, -1));
		this.promotedMoveSet.add(new LineMove( 0,  1));
		
		this.promotedMoveSet.add(new JumpMove(-1, -1));
		this.promotedMoveSet.add(new JumpMove(-1,  1));
		this.promotedMoveSet.add(new JumpMove( 1, -1));
		this.promotedMoveSet.add(new JumpMove( 1,  1));
	}

	@Override
	public String toChar() {
		return " " + (this.color == Piece.Color.BLACK ? "R" : "r");
	}

	@Override
	public Piece deepCopy() {
		Piece retval = new Rook(this.x, this.y, this.color);
		retval.isPromoted = this.isPromoted;
		return retval;
	}
	
}
