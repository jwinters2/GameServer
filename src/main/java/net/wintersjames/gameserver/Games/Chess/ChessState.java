package net.wintersjames.gameserver.Games.Chess;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import net.wintersjames.gameserver.Games.Chess.ChessPieces.Bishop;
import net.wintersjames.gameserver.Games.Chess.ChessPieces.King;
import net.wintersjames.gameserver.Games.Chess.ChessPieces.Knight;
import net.wintersjames.gameserver.Games.Chess.ChessPieces.Pawn;
import net.wintersjames.gameserver.Games.Chess.ChessPieces.Piece;
import net.wintersjames.gameserver.Games.Chess.ChessPieces.Queen;
import net.wintersjames.gameserver.Games.Chess.ChessPieces.Rook;
import net.wintersjames.gameserver.Games.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author james
 */
public class ChessState extends GameState implements Serializable {
	
	Logger logger = LoggerFactory.getLogger(ChessState.class);
	
	private List<Piece> pieces;
	private boolean whiteToMove;
	private Integer pendingPromotionFrom;
	
	private Integer enPassantTargetX;
	private Integer enPassantTargetY;
	private Piece.Color enPassantTargetColor;
	
	private Piece lastMovedLastPosition;
	
	private ChessState(String type) {
		super(type);
	}
	
	public ChessState() {
		super("chessState");
		
		this.whiteToMove = true;
		this.pieces = new ArrayList<>();
		this.pendingPromotionFrom = null;
		
		this.enPassantTargetX = null;
		this.enPassantTargetY = null;
		this.enPassantTargetColor = null;
		
		this.lastMovedLastPosition = null;

		// setup initial board
		for(int i=0; i<8; i++) {
			this.pieces.add(new Pawn(i, 1, Piece.Color.WHITE));
			this.pieces.add(new Pawn(i, 6, Piece.Color.BLACK));
		}
		
		this.pieces.add(new Rook	(0, 0, Piece.Color.WHITE));
		this.pieces.add(new Knight	(1, 0, Piece.Color.WHITE));
		this.pieces.add(new Bishop	(2, 0, Piece.Color.WHITE));
		this.pieces.add(new Queen	(3, 0, Piece.Color.WHITE));
		this.pieces.add(new King	(4, 0, Piece.Color.WHITE));
		this.pieces.add(new Bishop	(5, 0, Piece.Color.WHITE));
		this.pieces.add(new Knight	(6, 0, Piece.Color.WHITE));
		this.pieces.add(new Rook	(7, 0, Piece.Color.WHITE));
		
		this.pieces.add(new Rook	(0, 7, Piece.Color.BLACK));
		this.pieces.add(new Knight	(1, 7, Piece.Color.BLACK));
		this.pieces.add(new Bishop	(2, 7, Piece.Color.BLACK));
		this.pieces.add(new Queen	(3, 7, Piece.Color.BLACK));
		this.pieces.add(new King	(4, 7, Piece.Color.BLACK));
		this.pieces.add(new Bishop	(5, 7, Piece.Color.BLACK));
		this.pieces.add(new Knight	(6, 7, Piece.Color.BLACK));
		this.pieces.add(new Rook	(7, 7, Piece.Color.BLACK));
	}
	
	public ChessState(ChessState other) {
		super("chessState");

		this.pieces = other.getPiecesDeepCopy();
		this.whiteToMove = other.isWhiteToMove();
		this.pendingPromotionFrom = other.pendingPromotionFrom;
		
		this.enPassantTargetX = other.enPassantTargetX;
		this.enPassantTargetY = other.enPassantTargetX;
		this.enPassantTargetColor = other.enPassantTargetColor;
		
		this.lastMovedLastPosition = other.lastMovedLastPosition == null ? null : other.lastMovedLastPosition.deepCopy();
	}

	public List<Piece> getPieces() {
		return pieces;
	}
	
	public List<Piece> getPiecesDeepCopy() {
		ArrayList<Piece> retval = new ArrayList<>();
		for(Piece p: pieces) {
			if(p != null) {
				retval.add(p.deepCopy());
			}
		}
		return retval;
	}

	public boolean isWhiteToMove() {
		return whiteToMove;
	}

	public Integer getPendingPromotionFrom() {
		return pendingPromotionFrom;
	}
	
	public void setPendingPromotionFrom(Integer pendingPromotionFrom) {
		this.pendingPromotionFrom = pendingPromotionFrom;
	}
	
	public Piece getPieceAt(int x, int y) {
		for(Piece piece: pieces) {
			if(piece.getX() == x && piece.getY() == y) {
				return piece;
			}
		}
		return null;
	}
	
	public Piece getPieceAt(String pos) {
		int x = pos.charAt(0) - 'a';
		int y = pos.charAt(1) - '1';
		return getPieceAt(x, y);
	}
	
	@Override
	public String toString() {
		
		Piece[][] grid = new Piece[8][8];
		for(Piece piece: pieces) {
			grid[piece.getY()][piece.getX()] = piece;
		}
		
		String retval = "";
		for(int file = 0; file < 8; file++) {
			retval += "   " + (char)('a' + file);
		}
		retval += "\n";
		
		retval += " \u250C";
		for(int file = 6; file >= 0; file--) {
			retval += "\u2500\u2500\u2500\u252C";
		}
		retval += "\u2500\u2500\u2500\u2510\n";
		
		for(int rank = 7; rank >= 0; rank--) {
			retval += Integer.toString(rank + 1);
			for(int file = 0; file < 8; file++) {
				retval += "\u2502 "
				+ (grid[rank][file] != null ? grid[rank][file].toChar() : " ") 
				+ " ";
			}
			retval += "\u2502" + Integer.toString(rank + 1) + "\n";
			
			// only print the crosses when not the last rank
			if(rank > 0) {
				retval += " \u251C";
				for(int file = 6; file >= 0; file--) {
					retval += "\u2500\u2500\u2500\u253C";
				}
				retval += "\u2500\u2500\u2500\u2524\n";
			} else {
				retval += " \u2514";
				for(int file = 6; file >= 0; file--) {
					retval += "\u2500\u2500\u2500\u2534";
				}
				retval += "\u2500\u2500\u2500\u2518\n";
			}

		}
		
		for(int file = 0; file < 8; file++) {
			retval += "   " + (char)('a' + file);
		}
		retval += "\n";
		
		retval += (this.whiteToMove ? "White" : "Black") + " to move";
		
		return retval;
	}

	public void captureAt(String pos) {
		int x = pos.charAt(0) - 'a';
		int y = pos.charAt(1) - '1';
		
		captureAt(x, y);
	}
	
	public void captureAt(int x, int y) {
		
		Piece toRemove = getPieceAt(x, y);
		if(toRemove != null) {
			logger.info("removing {}", toRemove);
			pieces.remove(toRemove);
		}
	}

	public void move(String fromPos, String toPos) {
		int fromX = fromPos.charAt(0) - 'a';
		int fromY = fromPos.charAt(1) - '1';
		
		int toX = toPos.charAt(0) - 'a';
		int toY = toPos.charAt(1) - '1';
		
		move(fromX, fromY, toX, toY);
	}
		
	public void move(int fromX, int fromY, int toX, int toY) {	
		
		Piece toCapture = getPieceAt(toX, toY);
		Piece toMove = getPieceAt(fromX, fromY);
		
		
		if(toMove != null) {
			// record that this is the piece that last moved
			this.lastMovedLastPosition = toMove.deepCopy();
			//logger.info("moving {}",toMove);
			toMove.move(toX, toY);
		}
		
		// check for castling
		if(toMove instanceof King) {
			// kingside
			if(toX == fromX + 2) {
				Piece castlingRook = getPieceAt(fromX + 3, fromY);
				if(castlingRook != null) {
					move(fromX + 3, fromY, fromX + 1, fromY);
				}
			}
			// queenside
			if(toX == fromX - 2) {
				Piece castlingRook = getPieceAt(fromX - 4, fromY);
				if(castlingRook != null) {
					move(fromX - 4, fromY, fromX - 1, fromY);
				}
			}
		}
		
		// check for opening up an en passant capture
		if(toMove instanceof Pawn && Math.abs(fromY - toY) == 2) {
			this.enPassantTargetX = fromX;
			this.enPassantTargetY = (fromY + toY)/2;
			this.enPassantTargetColor = toMove.getColor();
			
			logger.info("en passant possible next turn ({}, {}, {})",
					this.enPassantTargetX,
					this.enPassantTargetY,
					this.enPassantTargetColor.name());
		}
		
		// check for capturing en passant
		if(toMove instanceof Pawn && fromX != toX && toCapture == null) {
			captureAt(toX, fromY);
		}		
	}
	
	public boolean canMove(String fromPos, String toPos, boolean isWhite) {
		
		int fromX = fromPos.charAt(0) - 'a';
		int fromY = fromPos.charAt(1) - '1';
		
		int toX = toPos.charAt(0) - 'a';
		int toY = toPos.charAt(1) - '1';
		
		return canMove(fromX, fromY, toX, toY, isWhite);
	}
		
	public boolean canMove(int fromX, int fromY, int toX, int toY, boolean isWhite) {
		
		// check if the position is in bounds
		if(fromX < 0 || fromX >= 8 || fromY < 0 || fromY >= 8) {
			return false;
		}
		
		Piece pieceToMove = getPieceAt(fromX, fromY);
		
		Piece.Color color = isWhite ? Piece.Color.WHITE : Piece.Color.BLACK;
		
		// a piece must exist here, it must be the person to move's color,
		// and it has to be their turn
		if(pieceToMove != null 
			&& pieceToMove.getColor() == color
			&& (color == Piece.Color.WHITE) == this.whiteToMove) {
			
			// check if the destination is in bounds
			if(toX < 0 || toX >= 8 || toY < 0 || toY >= 8) {
				return false;
			}
			
			Piece pieceToCapture = getPieceAt(toX, toY);
			// if there's a piece in the square we're moving to, and it's the same
			// color as the moving piece, it's an invalid move
			if(pieceToCapture != null && pieceToCapture.getColor() == color) {
				return false;
			}
			
			// we're not allowed to move into check
			ChessState nextState = new ChessState(this);
			nextState.move(fromX, fromY, toX, toY);
			if(nextState.isInCheck(color)) {
				return false;
			}
			
			return pieceToMove.canMove(toX, toY, this);
		}
		
		return false;
	}
	
	public boolean isSquareUnderAttack(int x, int y, Piece.Color attackingColor) {
		
		for(Piece piece: this.pieces) {
			if(piece.getColor() == attackingColor && piece.canMove(x, y, this)) {
				//logger.info("attacking piece {}", piece);
				return true;
			}
		}
		return false;
	}
	
	public boolean isInCheck(Piece.Color colorInCheck) {
		Piece.Color attackingColor = (colorInCheck == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE);
		for(Piece piece: pieces) {
			if(piece instanceof King && piece.getColor() == colorInCheck) {
				//logger.info("king piece: {}", piece);
				return isSquareUnderAttack(piece.getX(), piece.getY(), attackingColor);
			}
		}
		return false;
	}
	
	public boolean needsPromotion(String fromPos, String toPos) {
		int fromX = fromPos.charAt(0) - 'a';
		int fromY = fromPos.charAt(1) - '1';
		
		int toY = toPos.charAt(1) - '1';
		
		Piece piece = getPieceAt(fromX, fromY);
		if(piece instanceof Pawn) {
			int promotionRank = (piece.getColor() == Piece.Color.WHITE ? 7 : 0);
			if(toY == promotionRank) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean promote(String pos, String promoteTo) {
		
		int x = pos.charAt(0) - 'a';
		int y = pos.charAt(1) - '1';
		
		Piece piece = getPieceAt(x, y);
		
		Piece newPiece = null;
		if(promoteTo != null) {
			newPiece = switch(promoteTo) {
				case "knight" -> new Knight(piece.getX(), piece.getY(), piece.getColor());
				case "bishop" -> new Bishop(piece.getX(), piece.getY(), piece.getColor());
				case "rook"   -> new Rook  (piece.getX(), piece.getY(), piece.getColor());
				case "queen"  -> new Queen (piece.getX(), piece.getY(), piece.getColor());
				default -> null;
			};
		}

		if(newPiece != null) {
			pieces.remove(piece);
			pieces.add(newPiece);
			return true;
		}
		
		return false;
	}
	
	public boolean hasLegalMove() {
		
		Piece.Color colorToMove = this.whiteToMove ? Piece.Color.WHITE : Piece.Color.BLACK;
		
		for(Piece piece: this.pieces) {
			if(piece.getColor() == colorToMove && piece.hasLegalMove(this)) {
				logger.info("piece {} has legal move", piece);
				return true;
			}
		}
		
		return false;
	}
	
	public void nextMove() {
		this.whiteToMove = !this.whiteToMove;
	}
	
	public void resetEnPassant() {
		this.enPassantTargetX = null;
		this.enPassantTargetY = null;
		this.enPassantTargetColor = null;
	}

	public Integer getEnPassantTargetX() {
		return enPassantTargetX;
	}

	public Integer getEnPassantTargetY() {
		return enPassantTargetY;
	}

	public Piece.Color getEnPassantTargetColor() {
		return enPassantTargetColor;
	}

	public Piece getLastMovedLastPosition() {
		return lastMovedLastPosition;
	}
	
}
