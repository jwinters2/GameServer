package net.wintersjames.gameserver.Games.Go;

import java.util.ArrayList;
import java.util.List;
import net.wintersjames.gameserver.Games.GameState;

/**
 *
 * @author james
 */
public class GoState extends GameState {
	
	List<Stone> stones;
	private boolean whiteToMove;
	final int boardWidth = 19;
	
	public GoState() {
		super("goState");
		stones = new ArrayList<>();
		this.whiteToMove = false;
	}

	public List<Stone> getStones() {
		return stones;
	}

	public boolean isWhiteToMove() {
		return whiteToMove;
	}
	
	public Stone getStoneAt(int x, int y) {
		for(Stone stone: this.stones) {
			if(stone.getX() == x && stone.getY() == y) {
				return stone;
			}
		}
		return null;
	}

	public boolean canPlaceStone(int x, int y, Stone.Color movingColor) {
		
		if((movingColor == Stone.Color.WHITE) != whiteToMove) {
			return false;
		}
		
		if(x < 0 || x >= boardWidth || y < 0 || y >= boardWidth) {
			return false;
		}
		
		if(getStoneAt(x, y) != null) {
			return false;
		}
		
		// todo check for liberties
		
		return true;
	}

	public void placeStone(int x, int y, Stone.Color movingColor) {
		stones.add(new Stone(x, y, movingColor));
	}

	public void nextMove() {
		this.whiteToMove = !this.whiteToMove;
	}
	
	
}
