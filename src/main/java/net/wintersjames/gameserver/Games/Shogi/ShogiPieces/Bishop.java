package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.JumpMove;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.LineMove;

/**
 *
 * @author james
 */
public class Bishop extends Piece {

	public Bishop(int x, int y, Color color) {
		super(x, y, color, "bishop");
		
		this.moveSet.add(new LineMove(-1, -1));
		this.moveSet.add(new LineMove(-1,  1));
		this.moveSet.add(new LineMove( 1, -1));
		this.moveSet.add(new LineMove( 1,  1));
		
		this.promotedMoveSet.add(new LineMove(-1, -1));
		this.promotedMoveSet.add(new LineMove(-1,  1));
		this.promotedMoveSet.add(new LineMove( 1, -1));
		this.promotedMoveSet.add(new LineMove( 1,  1));
		
		this.promotedMoveSet.add(new JumpMove( 0, -1));
		this.promotedMoveSet.add(new JumpMove( 0,  1));
		this.promotedMoveSet.add(new JumpMove(-1,  0));
		this.promotedMoveSet.add(new JumpMove( 1,  0));
	}

	@Override
	public String toChar() {
		return " " + (this.color == Piece.Color.BLACK ? "B" : "b");
	}

	@Override
	public Piece deepCopy() {
		Piece retval = new Bishop(this.x, this.y, this.color);
		retval.isPromoted = this.isPromoted;
		return retval;
	}
	
}
