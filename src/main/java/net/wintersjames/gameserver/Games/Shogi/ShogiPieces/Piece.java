package net.wintersjames.gameserver.Games.Shogi.ShogiPieces;

import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Moves.MoveType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import net.wintersjames.gameserver.Games.Chess.ChessState;
import net.wintersjames.gameserver.Games.Shogi.ShogiState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author james
 */
public abstract class Piece implements Serializable {
	
	Logger logger = LoggerFactory.getLogger(Piece.class);
	
	public enum Color {
		BLACK,
		WHITE
	}
	
	protected int x;
	protected int y;
	protected Piece.Color color;
	final protected String type;
	boolean isPromoted;
	boolean inHand;
	
	protected transient List<MoveType> moveSet;
	protected transient List<MoveType> promotedMoveSet;
	
	public Piece(int x, int y, Piece.Color color, String type) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.type = type;
		this.isPromoted = false;
		this.inHand = false;
		
		this.moveSet = new ArrayList<>();
		this.promotedMoveSet = new ArrayList<>();
	}
	
	public abstract String toChar();
	public boolean canMove(int x, int y, ShogiState state) {
		
		List<MoveType> currentMoveset = this.isPromoted ? this.promotedMoveSet : this.moveSet;
		
		for(MoveType move: currentMoveset) {
			if(move.isMoveLegal(this, x, y, state)) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean canLionMove(int x, int y, ShogiState state) {
		return false;
	}

	public boolean hasLegalMove(ShogiState state) {
		
		List<MoveType> currentMoveset = this.isPromoted ? this.promotedMoveSet : this.moveSet;
		
		for(MoveType move: currentMoveset) {
			if(move.hasLegalMove(this, state)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void move(int x, int y, ShogiState state) {
		this.x = x;
		this.y = y;
	}
	
	public abstract Piece deepCopy();

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}

	public String getType() {
		return type;
	}

	public boolean getIsPromoted() {
		return isPromoted;
	}
	
	public boolean getInHand() {
		return inHand;
	}
	
	public void setInHand(boolean inHand) {
		this.inHand = inHand;
	}
	
	public void toggleColor() {
		this.color = (this.color == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE);
	}
	
	public void promote() {
		this.isPromoted = true;
	}
	
	public boolean getCanPromote() {
		return true;
	}

	public List<MoveType> getMoveSet() {
		return moveSet;
	}

	public List<MoveType> getPromotedMoveSet() {
		return promotedMoveSet;
	}
	
	public boolean isRoyal() {
		return false;
	}
	
	// in chu shogi, you can't just trade lions
	public boolean isTradeDisabled() {
		return false;
	}
	
	public boolean isSubstantial() {
		return true;
	}
	
	public boolean canTrade(ShogiState state, Piece toCapture) {
		return true;
	}
	
	public String getPromotesTo() {
		return null;
	}
	
	public boolean getCanPromoteOnFinalRank() {
		return false;
	}
	
	// some variants have pieces that can move twice in one turn
	// so depending on how we moved the first turn, the same player might have a second move to make
	public List<MoveType> getSecondLionMoves() {
		return null;
	}
}
