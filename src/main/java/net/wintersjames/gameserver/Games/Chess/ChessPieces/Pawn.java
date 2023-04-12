/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Chess.ChessPieces;

import net.wintersjames.gameserver.Games.Chess.ChessState;

/**
 *
 * @author james
 */
public class Pawn extends Piece {

	public Pawn(int x, int y, Color color) {
		super(x, y, color, "pawn");
	}

	@Override
	public char toChar() {
		return (color == Piece.Color.WHITE ? '\u265F' : '\u2659');
	}

	@Override
	public boolean canMove(int x, int y, ChessState state) {
		boolean retval = false;
		
		// white moves up, black moves down
		int direction = (color == Piece.Color.WHITE) ? 1 : -1;
		
		Piece pieceToCapture = state.getPieceAt(x, y);
		Piece intermediatePiece = state.getPieceAt(this.x, this.y + direction);
		
		// if we're moving one square forward, and not capturing
		retval |= this.y + direction == y && this.x == x && pieceToCapture == null;
		
		// if haven't moved, can move two squares;
		retval |= !this.hasMoved && this.y + (2 * direction) == y && this.x == x &&
				pieceToCapture == null && intermediatePiece == null;
		
		// we can move diagonally if we're capturing
		retval |= this.y + direction == y && this.x + 1 == x && 
				pieceToCapture != null && pieceToCapture.getColor() != getColor();
		retval |= this.y + direction == y && this.x - 1 == x && 
				pieceToCapture != null && pieceToCapture.getColor() != getColor();
		
		return retval;
	}

	@Override
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
		this.hasMoved = true;
	}
		
}
