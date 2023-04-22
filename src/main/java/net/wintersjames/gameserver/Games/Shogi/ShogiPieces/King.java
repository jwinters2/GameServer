package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.JumpMove;

/**
 *
 * @author james
 */
public class King extends Piece {
	public King(int x, int y, Piece.Color color) {
		super(x, y, color, "king");
		
		this.moveSet.add(new JumpMove(-1, 1));
		this.moveSet.add(new JumpMove( 0, 1));
		this.moveSet.add(new JumpMove( 1, 1));
		this.moveSet.add(new JumpMove(-1, 0));
		this.moveSet.add(new JumpMove( 1, 0));
		this.moveSet.add(new JumpMove(-1,-1));
		this.moveSet.add(new JumpMove( 0,-1));
		this.moveSet.add(new JumpMove( 1,-1));
	}

	@Override
	public String toChar() {
		return " " + (this.color == Piece.Color.BLACK ? "K" : "k");
	}

	@Override
	public Piece deepCopy() {
		return new King(this.x, this.y, this.color);
	}
}
