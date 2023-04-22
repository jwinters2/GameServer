package net.wintersjames.gameserver.Games.Shogi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Bishop;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Piece;
import net.wintersjames.gameserver.Games.GameState;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Gold;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.King;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Knight;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Lance;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Pawn;
import net.wintersjames.gameserver.Games.Shogi.ShogiPieces.Rook;
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
	
	private List<Integer> squaresToHighlight;
	
	// the piece we're waiting on whether or not to promote, if there is one
	private Piece pendingPromotion;
	
	public ShogiState() {
		super("shogiState");
		this.pieces = new ArrayList<>();
		this.whiteHand = new HashMap<>();
		this.blackHand = new HashMap<>();
	
		this.whiteToMove = true;
		this.pendingPromotion = null;
		this.squaresToHighlight = new ArrayList<>();
		
		for(int i=0; i<9; i++) {
			this.pieces.add(new Pawn(i, 2, Piece.Color.WHITE));
			this.pieces.add(new Pawn(i, 6, Piece.Color.BLACK));
		}
		
		this.pieces.add(new Rook(7, 1, Piece.Color.WHITE));
		this.pieces.add(new Bishop(1, 1, Piece.Color.WHITE));
		
		this.pieces.add(new Bishop(7, 7, Piece.Color.BLACK));
		this.pieces.add(new Rook(1, 7, Piece.Color.BLACK));
		
		this.pieces.add(new Lance(0, 0, Piece.Color.WHITE));
		this.pieces.add(new Knight(1, 0, Piece.Color.WHITE));
		this.pieces.add(new Silver(2, 0, Piece.Color.WHITE));
		this.pieces.add(new Gold(3, 0, Piece.Color.WHITE));
		this.pieces.add(new King(4, 0, Piece.Color.WHITE));
		this.pieces.add(new Gold(5, 0, Piece.Color.WHITE));
		this.pieces.add(new Silver(6, 0, Piece.Color.WHITE));
		this.pieces.add(new Knight(7, 0, Piece.Color.WHITE));
		this.pieces.add(new Lance(8, 0, Piece.Color.WHITE));
		
		this.pieces.add(new Lance(0, 8, Piece.Color.BLACK));
		this.pieces.add(new Knight(1, 8, Piece.Color.BLACK));
		this.pieces.add(new Silver(2, 8, Piece.Color.BLACK));
		this.pieces.add(new Gold(3, 8, Piece.Color.BLACK));
		this.pieces.add(new King(4, 8, Piece.Color.BLACK));
		this.pieces.add(new Gold(5, 8, Piece.Color.BLACK));
		this.pieces.add(new Silver(6, 8, Piece.Color.BLACK));
		this.pieces.add(new Knight(7, 8, Piece.Color.BLACK));
		this.pieces.add(new Lance(8, 8, Piece.Color.BLACK));
	}
	
	public ShogiState(ShogiState other) {
		super("shogiState");
		this.pieces = this.getDeepCopy(other.pieces);
		this.whiteHand = new HashMap<>(other.whiteHand);
		this.blackHand = new HashMap<>(other.blackHand);
	
		this.whiteToMove = other.whiteToMove;
		this.pendingPromotion = other.pendingPromotion;
		this.squaresToHighlight = other.squaresToHighlight;
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
			
			Map<String, Integer> hand = toRemove.getColor() == Piece.Color.WHITE ? this.whiteHand : this.blackHand;

			if(hand.containsKey(toRemove.getType())) {
				int count = hand.get(toRemove.getType());
				hand.put(toRemove.getType(), count + 1);
			} else {
				hand.put(toRemove.getType(), 1);
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
		
		// a piece can't exist here, can't be the other person's color,
		// and it can't be the opponent's turn
		if(pieceToMove == null 
			|| pieceToMove.getColor() != color
			|| (color == Piece.Color.WHITE) != this.whiteToMove) {
			return false;
		}
		
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
			logger.info("(move) king is in check");
			return false;
		}

		return pieceToMove.canMove(toX, toY, this);
	}
	
	public boolean canDrop(int toX, int toY, String pieceType, boolean isWhite) {
		
		// we can't drop on another piece
		if(getPieceAt(toX, toY) != null) {
			return false;
		}
		
		Piece.Color color = isWhite ? Piece.Color.WHITE : Piece.Color.BLACK;
		
		// it can't be the opponent's turn
		if((color == Piece.Color.WHITE) != this.whiteToMove) {
			return false;
		}
		
		Map<String, Integer> hand = color == Piece.Color.WHITE ? this.whiteHand : this.blackHand;
		if(!hand.containsKey(pieceType)) {
			// we can't drop a piece we don't have
			return false;
		}
		
		int lastRank = (color == Piece.Color.WHITE ? 8 : 0);
		int penultimateRank = (color == Piece.Color.WHITE ? 7 : 1);
		
		// pawns, knights and lances can't drop on the last rank
		if(toY == lastRank && (pieceType.equals("pawn") || pieceType.equals("knight") || pieceType.equals("lance"))) {
			return false;
		}
		
		// knights also can't drop on the second to last rank
		if(toY == penultimateRank && pieceType.equals("knight")) {
			return false;
		}
		
		// pawns can't drop on a file that already has a pawn
		if(pieceType.equals("pawn")) {
			for(int y = 0; y < 9; y++) {
				Piece p = getPieceAt(toX, y);
				if(p instanceof Pawn && !p.getIsPromoted() && p.getColor() == color) {
					return false;
				}
			}
		}
		
		// pawns can't checkmate
		if(pieceType.equals("pawn")) {
			ShogiState nextState = new ShogiState(this);
			nextState.drop(toX, toY, pieceType, isWhite);
			nextState.nextMove();
			if(!nextState.hasLegalMove()) {
				logger.info("illegal checkmate by pawn drop");
				return false;
			}
		}
		
		// we're not allowed to drop a piece that leaves us in check
		ShogiState nextState = new ShogiState(this);
		nextState.drop(toX, toY, pieceType, isWhite);
		if(nextState.isInCheck(color)) {
			logger.info("(drop) king is in check");
			return false;
		}


		return true;
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
		
		if(isPromotionMandatory(toX, toY, toMove.getType(), toMove.getColor())) {
			toMove.promote();
		}
	}
	
	public void drop(int toX, int toY, String pieceType, boolean isWhite) {
		
		Piece.Color color = isWhite ? Piece.Color.WHITE : Piece.Color.BLACK;
		
		Piece droppedPiece = switch (pieceType) {
			case "pawn" -> new Pawn(toX, toY, color);
			case "rook" -> new Rook(toX, toY, color);
			case "bishop" -> new Bishop(toX, toY, color);
			case "lance" -> new Lance(toX, toY, color);
			case "knight" -> new Knight(toX, toY, color);
			case "silver" -> new Silver(toX, toY, color);
			case "gold" -> new Gold(toX, toY, color);
			default -> null;
		};
		
		this.pieces.add(droppedPiece);
		Map<String, Integer> hand = (color == Piece.Color.WHITE ? this.whiteHand : this.blackHand);
		
		int count = hand.get(pieceType) - 1;
		if(count == 0) {
			hand.remove(pieceType);
		} else {
			hand.put(pieceType, count);
		}
	}
	
	public boolean isPromotionOptional(int fromX, int fromY, int toX, int toY) {
		
		Piece toPromote = getPieceAt(toX, toY);
		
		// null pieces, already-promoted pieces, gold generals and kings can't promote
		if(toPromote == null 
			|| toPromote.getIsPromoted() 
			|| toPromote.getType().equals("king")
			|| toPromote.getType().equals("gold")) {
			logger.info("promotion not allowed here, isPromoted={}, type={}",
				toPromote == null ? null : toPromote.getIsPromoted(),
				toPromote == null ? null : toPromote.getType()
			);
			return false;
		}
		
		// piece must either start or end in their promotion zone
		if(toPromote.getColor() == Piece.Color.WHITE) {
			if(fromY < 6 && toY < 6) {
				logger.info("y ({} & {}) >= 6", fromY, toY);
				return false;
			}
		} else {
			if(fromY > 2 && toY > 2) {
				logger.info("y ({} & {}) <= 2", fromY, toY);
				return false;
			}
		}
		
		if(isPromotionMandatory(toX, toY, toPromote.getType(), toPromote.getColor())) {
			logger.info("promotion is mandatory");
			return false;
		}
		
		return true;
	}
	
	public boolean isPromotionMandatory(int toX, int toY, String pieceType, Piece.Color color) {
		
		int lastRank = (color == Piece.Color.WHITE ? 8 : 0);
		
		// pawns, knights and lances can't drop on the last rank
		if(toY == lastRank && (pieceType.equals("pawn") || pieceType.equals("knight") || pieceType.equals("lance"))) {
			return true;
		}
		
		int penultimateRank = (color == Piece.Color.WHITE ? 7 : 1);
		
		// knights also can't drop on the second to last rank
		if(toY == penultimateRank && pieceType.equals("knight")) {
			return true;
		}
		
		return false;
	}
	
	public boolean hasLegalMove() {
		logger.info("checking for legal move");
		
		Piece.Color colorToMove = this.whiteToMove ? Piece.Color.WHITE : Piece.Color.BLACK;
		
		// check for moving pieces
		for(Piece piece: this.pieces) {
			if(piece.getColor() == colorToMove && piece.hasLegalMove(this)) {
				logger.info("piece {} has legal move", piece);
				return true;
			}
		}
		
		// check for drops
		Map<String, Integer> hand = (colorToMove == Piece.Color.WHITE ? this.whiteHand : this.blackHand);
		for(String pieceType: hand.keySet()) {
			for(int x = 0; x < 9; x++) {
				for(int y = 0; y < 9; y++) {
					if(canDrop(x, y, pieceType, colorToMove == Piece.Color.WHITE)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public void setPendingPromotion(int x, int y) {
		this.pendingPromotion = getPieceAt(x, y);
	}
	
	public void resetPendingPromotion() {
		this.pendingPromotion = null;
	}
	
	public void setSquaresToHighlight(int...coords) {
		this.squaresToHighlight.clear();
		for(int coord: coords) {
			this.squaresToHighlight.add(coord);
		}
	}

	public List<Integer> getSquaresToHighlight() {
		return squaresToHighlight;
	}

	public Piece getPendingPromotion() {
		return pendingPromotion;
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
