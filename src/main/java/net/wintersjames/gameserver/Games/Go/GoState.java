package net.wintersjames.gameserver.Games.Go;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.wintersjames.gameserver.Games.GameState;

/**
 *
 * @author james
 */
public class GoState extends GameState {
	
	List<Stone> stones;
	private boolean whiteToMove;
	final int boardWidth = 19;
	char[][] territory;
	
	public GoState() {
		super("goState");
		stones = new ArrayList<>();
		this.whiteToMove = false;
		this.territory = new char[boardWidth][boardWidth];
		
		
		stones.add(new Stone(16, 7, Stone.Color.BLACK));
		stones.add(new Stone(1, 12, Stone.Color.WHITE));
		stones.add(new Stone(15, 6, Stone.Color.BLACK));
		stones.add(new Stone(2, 13, Stone.Color.WHITE));
		stones.add(new Stone(14, 7, Stone.Color.BLACK));
		stones.add(new Stone(3, 12, Stone.Color.WHITE));
		stones.add(new Stone(13, 6, Stone.Color.BLACK));
		stones.add(new Stone(4, 13, Stone.Color.WHITE));
		stones.add(new Stone(12, 7, Stone.Color.BLACK));
		stones.add(new Stone(5, 12, Stone.Color.WHITE));
		stones.add(new Stone(16, 8, Stone.Color.BLACK));
		stones.add(new Stone(5, 11, Stone.Color.WHITE));
		stones.add(new Stone(12, 8, Stone.Color.BLACK));
		stones.add(new Stone(1, 11, Stone.Color.WHITE));
		stones.add(new Stone(15, 9, Stone.Color.BLACK));
		stones.add(new Stone(2, 10, Stone.Color.WHITE));
		stones.add(new Stone(13, 9, Stone.Color.BLACK));
		stones.add(new Stone(4, 10, Stone.Color.WHITE));
		stones.add(new Stone(14, 10, Stone.Color.BLACK));
		stones.add(new Stone(3, 9, Stone.Color.WHITE));
		stones.add(new Stone(15, 18, Stone.Color.BLACK));
		stones.add(new Stone(2, 0, Stone.Color.WHITE));
		stones.add(new Stone(14, 17, Stone.Color.BLACK));
		stones.add(new Stone(1, 1, Stone.Color.WHITE));
		stones.add(new Stone(13, 17, Stone.Color.BLACK));
		stones.add(new Stone(2, 2, Stone.Color.WHITE));
		stones.add(new Stone(12, 18, Stone.Color.BLACK));
		stones.add(new Stone(3, 2, Stone.Color.WHITE));
		stones.add(new Stone(11, 17, Stone.Color.BLACK));
		stones.add(new Stone(4, 1, Stone.Color.WHITE));
		stones.add(new Stone(10, 18, Stone.Color.BLACK));
		stones.add(new Stone(5, 6, Stone.Color.WHITE));
		stones.add(new Stone(15, 13, Stone.Color.BLACK));
		stones.add(new Stone(5, 5, Stone.Color.WHITE));
		stones.add(new Stone(15, 14, Stone.Color.BLACK));
		stones.add(new Stone(5, 4, Stone.Color.WHITE));
		stones.add(new Stone(15, 12, Stone.Color.BLACK));
		stones.add(new Stone(6, 7, Stone.Color.WHITE));
		stones.add(new Stone(14, 15, Stone.Color.BLACK));
		stones.add(new Stone(7, 7, Stone.Color.WHITE));
		stones.add(new Stone(14, 11, Stone.Color.BLACK));
		stones.add(new Stone(8, 7, Stone.Color.WHITE));
		stones.add(new Stone(13, 11, Stone.Color.BLACK));
		stones.add(new Stone(9, 6, Stone.Color.WHITE));
		stones.add(new Stone(12, 11, Stone.Color.BLACK));
		stones.add(new Stone(9, 5, Stone.Color.WHITE));
		stones.add(new Stone(11, 11, Stone.Color.BLACK));
		stones.add(new Stone(9, 4, Stone.Color.WHITE));
		stones.add(new Stone(10, 12, Stone.Color.BLACK));
		stones.add(new Stone(8, 3, Stone.Color.WHITE));
		stones.add(new Stone(10, 13, Stone.Color.BLACK));
		stones.add(new Stone(7, 3, Stone.Color.WHITE));
		stones.add(new Stone(10, 14, Stone.Color.BLACK));
		stones.add(new Stone(6, 3, Stone.Color.WHITE));
		
		this.calculateTerritory();
	}

	public List<Stone> getStones() {
		return stones;
	}

	public boolean isWhiteToMove() {
		return whiteToMove;
	}
	
	public char[][] getTerritory() {
		return territory;
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
		
		if(!isInBounds(x, y)) {
			return false;
		}
		
		if(getStoneAt(x, y) != null) {
			return false;
		}
		
		// todo check for liberties
		
		return true;
	}
	
	public boolean isInBounds(int x, int y) {
		return(x >= 0 && x < this.boardWidth && y >= 0 && y < this.boardWidth);
	}

	public void placeStone(int x, int y, Stone.Color movingColor) {
		stones.add(new Stone(x, y, movingColor));
	}

	public void nextMove() {
		this.whiteToMove = !this.whiteToMove;
	}

	void calculateLiberties() {
		for(Stone stone: this.stones) {
			stone.calculateLiberties(this);
		}
	}
	
	void calculateTerritory() {
		for(int x = 0; x < this.boardWidth; x++) {
			for(int y = 0; y < this.boardWidth; y++) {
				
				Set<Stone.Color> borders = new HashSet<>();
				Set<String> squaresChecked = new HashSet<>();
				
				calculateTerritory(x, y, squaresChecked, borders);
				
				if(borders.size() == 1) {
					territory[x][y] = borders.contains(Stone.Color.WHITE) ? 'w' : 'b';
				} else {
					territory[x][y] = '-';
				}
			}
		}
	}
	
	private void calculateTerritory(int x, int y, Set<String> squaresChecked, Set<Stone.Color> border) {
		
		String coord = Integer.toString(x) + "," + Integer.toString(y);
		
		if(squaresChecked.contains(coord) || getStoneAt(x, y) != null) {
			return;
		}
		squaresChecked.add(coord);
		
		for(int i = 0; i < 4; i++) {
			/*
				  0
				3 . 1
				  2
			*/
			int nextX = x + (i == 3 ? 1 : (i == 1 ? -1 : 0));
			int nextY = y + (i == 2 ? 1 : (i == 0 ? -1 : 0));

			if(isInBounds(nextX, nextY)) {

				Stone newStone = getStoneAt(nextX, nextY);

				if(newStone != null) {
					border.add(newStone.getColor());
				} else {
					calculateTerritory(nextX, nextY, squaresChecked, border);
				}

			}
		}
	}
	
	
}
