package net.wintersjames.gameserver.Games.Shogi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Piece;
import net.wintersjames.gameserver.Games.GameState;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Gold;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.King;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Knight;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Pawn;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Silver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author james
 */
public class ShogiState extends GameState implements Serializable {
	
	Logger logger = LoggerFactory.getLogger(ShogiState.class);
	
	private List<Piece> pieces;
	private Map<String, Integer> whiteHand;
	private Map<String, Integer> blackHand;
	private boolean whiteToMove;
	
	public ShogiState() {
		super("shogiState");
		this.pieces = new ArrayList<>();
		this.whiteHand = new HashMap<>();
		this.blackHand = new HashMap<>();
	
		this.whiteToMove = true;
		
		for(int i=0; i<9; i++) {
			this.pieces.add(new Pawn(i, 2, Piece.Color.WHITE));
			this.pieces.add(new Pawn(i, 6, Piece.Color.BLACK));
		}
		
		if(pieces.get(11).getColor() == Piece.Color.BLACK) {
			pieces.get(11).promote();
		}

		//this.pieces.add(new Bishop(1, 1, Piece.Color.WHITE));
		//this.pieces.add(new Rook(1, 7, Piece.Color.WHITE));
		
		//this.pieces.add(new Rook(7, 1, Piece.Color.BLACK));
		//this.pieces.add(new Bishop(7, 7, Piece.Color.BLACK));
		
		//this.pieces.add(new Lance(0, 0, Piece.Color.WHITE));
		this.pieces.add(new Knight(1, 0, Piece.Color.WHITE));
		this.pieces.add(new Silver(2, 0, Piece.Color.WHITE));
		this.pieces.add(new Gold(3, 0, Piece.Color.WHITE));
		this.pieces.add(new King(4, 0, Piece.Color.WHITE));
		this.pieces.add(new Gold(5, 0, Piece.Color.WHITE));
		this.pieces.add(new Silver(6, 0, Piece.Color.WHITE));
		this.pieces.add(new Knight(7, 0, Piece.Color.WHITE));
		//this.pieces.add(new Lance(8, 0, Piece.Color.WHITE));
		
		//this.pieces.add(new Lance(0, 0, Piece.Color.WHITE));
		this.pieces.add(new Knight(1, 8, Piece.Color.BLACK));
		this.pieces.add(new Silver(2, 8, Piece.Color.BLACK));
		this.pieces.add(new Gold(3, 8, Piece.Color.BLACK));
		this.pieces.add(new King(4, 8, Piece.Color.BLACK));
		this.pieces.add(new Gold(5, 8, Piece.Color.BLACK));
		this.pieces.add(new Silver(6, 8, Piece.Color.BLACK));
		this.pieces.add(new Knight(7, 8, Piece.Color.BLACK));
		//this.pieces.add(new Lance(8, 0, Piece.Color.WHITE));
		
	}
	
	public ShogiState(ShogiState other) {
		super("shogiState");
		this.pieces = this.getDeepCopy(other.pieces);
		this.whiteHand = new HashMap<>(other.whiteHand);
		this.blackHand = new HashMap<>(other.blackHand);
	
		this.whiteToMove = other.whiteToMove;
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public Map<String, Integer> getWhiteHand() {
		return whiteHand;
	}

	public Map<String, Integer> getBlackHand() {
		return blackHand;
	}
	
	public boolean isWhiteToMove() {
		return whiteToMove;
	}
	
	public Piece getPieceAt(int x, int y) {
		for(Piece piece: pieces) {
			if(piece.getX() == x && piece.getY() == y) {
				return piece;
			}
		}
		return null;
	}
	
	public void captureAt(int x, int y) {
		
		Piece toRemove = getPieceAt(x, y);
		if(toRemove != null) {
			
			logger.info("removing {}", toRemove);
			
			// removefrom board
			pieces.remove(toRemove);
			
			// add to hand
			toRemove.setInHand(true);
			toRemove.toggleColor();
			if(toRemove.getColor() == Piece.Color.WHITE) {
				if(this.whiteHand.containsKey(toRemove.getType())) {
					int count = this.whiteHand.get(toRemove.getType());
					this.whiteHand.put(toRemove.getType(), count + 1);
				} else {
					this.whiteHand.put(toRemove.getType(), 1);
				}
			} else {
				if(this.blackHand.containsKey(toRemove.getType())) {
					int count = this.blackHand.get(toRemove.getType());
					this.blackHand.put(toRemove.getType(), count + 1);
				} else {
					this.blackHand.put(toRemove.getType(), 1);
				}
			}
		}
	}
	
	public boolean canMove(int fromX, int fromY, int toX, int toY, boolean isWhite) {
		
		// check if the position is in bounds
		if(fromX < 0 || fromX >= 9 || fromY < 0 || fromY >= 9) {
			logger.info("piece is out of bounds");
			return false;
		}
		
		Piece pieceToMove = getPieceAt(fromX, fromY);
		if(pieceToMove != null) {
			logger.info("piece: {}, color: {}", pieceToMove.getType(), pieceToMove.getColor());
		} else {		
			logger.info("piece to move is null");
		}

		
		Piece.Color color = isWhite ? Piece.Color.WHITE : Piece.Color.BLACK;
		
		// a piece must exist here, it must be the person to move's color,
		// and it has to be their turn
		if(pieceToMove != null 
			&& pieceToMove.getColor() == color
			&& (color == Piece.Color.WHITE) == this.whiteToMove) {
			
			// check if the destination is in bounds
			if(toX < 0 || toX >= 9 || toY < 0 || toY >= 9) {
				logger.info("piece destination out of bounds");
				return false;
			}
			
			Piece pieceToCapture = getPieceAt(toX, toY);
			// if there's a piece in the square we're moving to, and it's the same
			// color as the moving piece, it's an invalid move
			if(pieceToCapture != null && pieceToCapture.getColor() == color) {
				logger.info("cannot capture our own pieces");
				return false;
			}
			
			// we're not allowed to move into check
			ShogiState nextState = new ShogiState(this);
			nextState.move(fromX, fromY, toX, toY);
			if(nextState.isInCheck(color)) {
				logger.info("king is in check");
				return false;
			}
			
			return pieceToMove.canMove(toX, toY, this);
		}
		
		return false;
	}
	
	public boolean isSquareUnderAttack(int x, int y, Piece.Color attackingColor) {
		
		for(Piece piece: this.pieces) {
			if(piece.getColor() == attackingColor && piece.canMove(x, y, this)) {
				logger.info("attacking piece {}", piece);
				return true;
			}
		}
		return false;
	}
	
	public boolean isInCheck(Piece.Color colorInCheck) {
		Piece.Color attackingColor = (colorInCheck == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE);
		for(Piece piece: pieces) {
			if(piece instanceof King && piece.getColor() == colorInCheck) {
				logger.info("king piece: {}", piece);
				return isSquareUnderAttack(piece.getX(), piece.getY(), attackingColor);
			}
		}
		return false;
	}
	
	public void move(int fromX, int fromY, int toX, int toY) {
		logger.info("moving ({},{}) to ({},{})", fromX, fromY, toX, toY);
		this.captureAt(toX, toY);
		Piece toMove = getPieceAt(fromX, fromY);
		toMove.move(toX, toY);
	}
	
	public void nextMove() {
		this.whiteToMove = !this.whiteToMove;
	}	

	private List<Piece> getDeepCopy(List<Piece> piecesToCopy) {
		ArrayList<Piece> retval = new ArrayList<>();
		for(Piece p: piecesToCopy) {
			if(p != null) {
				retval.add(p.deepCopy());
			}
		}
		return retval;
	}
}
