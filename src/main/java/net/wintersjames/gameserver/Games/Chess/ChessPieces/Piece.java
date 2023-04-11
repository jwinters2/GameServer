/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Chess.ChessPieces;

import java.io.Serializable;

/**
 *
 * @author james
 */
public abstract class Piece implements Serializable {
		
	public enum Color {
		BLACK,
		WHITE
	}
		
	protected int x;
	protected int y;
	protected boolean hasMoved;
	protected Color color;
	private String type;
	
	public Piece(int x, int y, Color color, String type) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.type = type;
		this.hasMoved = false;
	}
		
	public abstract char toChar();
	public abstract boolean canMove(int x, int y);
	public abstract void move(int x, int y);

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isHasMoved() {
		return hasMoved;
	}

	public Color getColor() {
		return color;
	}
}
