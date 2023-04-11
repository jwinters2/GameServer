/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Chess.ChessPieces;

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
	public boolean canMove(int x, int y) {
		return false;
	}

	@Override
	public void move(int x, int y) {

	}
		
}
