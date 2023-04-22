package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.JumpMove;

/**
 *
 * @author james
 */
public class Gold extends Piece {

	public Gold(int x, int y, Color color) {
		super(x, y, color, "gold");
		
		this.moveSet.add(new JumpMove(-1,  1));
		this.moveSet.add(new JumpMove( 0,  1));
		this.moveSet.add(new JumpMove( 1,  1));
		this.moveSet.add(new JumpMove(-1,  0));
		this.moveSet.add(new JumpMove( 1,  0));
		this.moveSet.add(new JumpMove( 0, -1));
	}

	@Override
	public String toChar() {
		return " " + (this.color == Piece.Color.BLACK ? "G" : "g");
	}

	@Override
	public Piece deepCopy() {
		return new Gold(this.x, this.y, this.color);
	}
	
}
