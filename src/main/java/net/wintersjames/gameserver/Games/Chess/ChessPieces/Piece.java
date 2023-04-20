package net.wintersjames.gameserver.Games.Chess.ChessPieces;

import java.io.Serializable;
import net.wintersjames.gameserver.Games.Chess.ChessState;
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
	protected boolean hasMoved;
	protected Color color;
	final private String type;
	
	public Piece(int x, int y, Color color, String type) {
		this.x = x;
		this.y = y;
		this.color = color;
		this.type = type;
		this.hasMoved = false;
	}
		
	public abstract char toChar();
	public abstract boolean canMove(int x, int y, ChessState state);
	public abstract boolean hasLegalMove(ChessState state);
	public abstract void move(int x, int y);
	public abstract Piece deepCopy();

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isHasMoved() {
		return hasMoved;
	}
	
	public boolean getHasMoved() {
		return hasMoved;
	}

	public Color getColor() {
		return color;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return "Piece{" + "x=" + x + ", y=" + y + ", hasMoved=" + hasMoved + ", color=" + color + ", type=" + type + '}';
	}
	
	// can this piece move to position (x,y) in direction (dx, dy) without being blocked
	final protected boolean canMoveInLine(int x, int y, int dx, int dy, ChessState state) {
		
		int xoffset = x - this.x;
		int yoffset = y - this.y;
		
		// unless one of the offsets is 0 (rook movement), 
		// their absolute values need to be equal (bishop movement)
		if(xoffset != 0 && yoffset != 0 && Math.abs(xoffset) != Math.abs(yoffset)) {
			return false;
		}
		
		// (dx,dy) must be the correct direction
		if(Math.signum(xoffset) != Math.signum(dx) || Math.signum(yoffset) != Math.signum(dy))
		{
			return false;
		}
		
		//logger.info("checking for blocking pieces (x,y,dx,dy)=({},{},{},{})", x, y, dx, dy);
		
		int moveDist = Math.max(Math.abs(xoffset), Math.abs(yoffset));
		for(int i=1; i<moveDist; i++) {
			int stepX = this.x + (i * (int)Math.signum(dx));
			int stepY = this.y + (i * (int)Math.signum(dy));
			
			//logger.info("checking ({},{}) for blocking pieces", stepX, stepY);
			
			Piece blockingPiece = state.getPieceAt(stepX, stepY);
			if(blockingPiece != null) {
				return false;
			}
		}
		
		return true;
	}
	
	final protected boolean hasLegalMoveInDirection(int dx, int dy, ChessState state) {
		
		if(dx == 0 && dy == 0) {
			return false;
		}
		
		int toX = this.x + dx;
		int toY = this.y + dy;
		while(toX >= 0 && toX < 8 && toY >= 0 && toY < 8) {
			
			if(state.canMove(this.x, this.y, toX, toY, this.color == Piece.Color.WHITE)) {
				return true;
			}
			
			toX += dx;
			toY += dy;
		}
		
		return false;
	}
}
