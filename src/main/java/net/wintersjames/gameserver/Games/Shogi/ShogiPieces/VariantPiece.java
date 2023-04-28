package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import java.util.List;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.JumpMove;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.LineMove;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.LionMove;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.MoveType;
import net.wintersjames.gameserver.Games.Shogi.ShogiState;

/**
 *
 * @author james
 */
public class VariantPiece extends Piece {
	
	String abbr;
	boolean canPromote;
	boolean isRoyal;
	boolean promotesToRoyal;
	List<MoveType> lionMoves;

	public VariantPiece(int x, int y, Color color, String type) {
		super(x, y, color, type);
		// calculate the abbreviation
		String[] words = type.split(" ");
		abbr = "";
		for(String word: words) {
			abbr += (word.substring(0, 1));
		}
	}

	@Override
	public String toChar() {
		return abbr;
	}

	@Override
	public Piece deepCopy() {
		VariantPiece retval = new VariantPiece(x, y, color, type);
		retval.isPromoted = this.isPromoted;
		retval.isRoyal = this.isRoyal;
		retval.promotesToRoyal = this.promotesToRoyal;
		retval.moveSet.addAll(this.moveSet);
		retval.promotedMoveSet.addAll(this.promotedMoveSet);
		return retval;
	}
	
	public void addJumpMove(int dx, int dy) {
		this.moveSet.add(new JumpMove(dx, dy));
	}
	
	public void addLineMove(int dx, int dy) {
		this.moveSet.add(new LineMove(dx, dy));
	}
	
	public void addPromotedJumpMove(int dx, int dy) {
		this.promotedMoveSet.add(new JumpMove(dx, dy));
	}
	
	public void addPromotedLineMove(int dx, int dy) {
		this.promotedMoveSet.add(new LineMove(dx, dy));
	}
	
	public void addMoves(List<MoveType> moves) {
		this.moveSet.addAll(moves);
	}
	
	public void addPromotedMoves(List<MoveType> moves) {
		this.promotedMoveSet.addAll(moves);
	}

	@Override
	public boolean getCanPromote() {
		return canPromote;
	}

	public void setCanPromote(boolean canPromote) {
		this.canPromote = canPromote;
	}
	 
	public void setIsRoyal(boolean isRoyal) {
		this.isRoyal = isRoyal;
	}
	
	public void setPromotesToRoyal(boolean promotesToRoyal) {
		this.promotesToRoyal = promotesToRoyal;
	}
	
	@Override
	public boolean isRoyal() {
		return this.isRoyal || (this.isPromoted && this.promotesToRoyal);
	}
	
	@Override
	public void move(int x, int y, ShogiState state) {
		
		List<MoveType> moves = (this.isPromoted ? this.promotedMoveSet : this.moveSet);
		
		this.lionMoves = null;
		
		// if we're not already expecing a second move, check if this move 
		// is eligable for a second move
		if(state.getPendingSecondMove() == null) {
			for(MoveType move: moves) {
				if(move instanceof LionMove && move.isMoveLegal(this, x, y, state)) {
					this.lionMoves = ((LionMove)move).getSecondMoves();
				}
			}
		}
		
		this.x = x;
		this.y = y;
	}
	
	// some variants have pieces that can move twice in one turn
	// so depending on how we moved the first turn, the same player might have a second move to make
	@Override
	public List<MoveType> getSecondLionMoves() {
		return lionMoves;
	}
	
	@Override
	public boolean canLionMove(int x, int y, ShogiState state) {

		if(this.lionMoves == null) {
			return false;
		}
		
		for(MoveType move: this.lionMoves) {
			if(move.isMoveLegal(this, x, y, state)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String toString() {
		return "VariantPiece{" + "abbr=" + abbr + ", canPromote=" + canPromote + ", isRoyal=" + isRoyal + ", promotesToRoyal=" + promotesToRoyal + ", lionMoves=" + lionMoves + '}';
	}
	
	
}
