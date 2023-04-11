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

/**
 *
 * @author james
 */
public class ChessState extends GameState implements Serializable {
	
	private List<Piece> pieces;
	private boolean whiteToMove;
	
	private ChessState(String type) {
		super(type);
	}
	
	public ChessState() {
		super("chessState");
		
		this.whiteToMove = true;
		this.pieces = new ArrayList<>();

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

		this.pieces = new ArrayList<>(other.getPieces());
		this.whiteToMove = other.isWhiteToMove();
	}

	public List<Piece> getPieces() {
		return pieces;
	}

	public boolean isWhiteToMove() {
		return whiteToMove;
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

	void captureAt(String pos) {
		int x = pos.charAt(0) - 'a';
		int y = pos.charAt(1) - '1';
		
		List<Piece> tempPieces = new ArrayList<>(pieces);
		for(Piece piece: tempPieces) {
			if(piece.getX() == x && piece.getY() == y) {
				System.out.println("removing " + piece);
				pieces.remove(piece);
				return;
			}
		}
	}

	void move(String fromPos, String toPos) {
		int fromX = fromPos.charAt(0) - 'a';
		int fromY = fromPos.charAt(1) - '1';
		
		int toX = toPos.charAt(0) - 'a';
		int toY = toPos.charAt(1) - '1';
		
		
		for(Piece piece: pieces) {
			if(piece.getX() == fromX && piece.getY() == fromY) {
				System.out.println("moving " + piece);
				piece.move(toX, toY);
				return;
			}
		}
	}
	
}
