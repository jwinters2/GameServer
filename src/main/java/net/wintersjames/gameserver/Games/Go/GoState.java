package net.wintersjames.gameserver.Games.Go;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.wintersjames.gameserver.Games.GameState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author james
 */
public class GoState extends GameState {
	
	Logger logger = LoggerFactory.getLogger(GoState.class);
	
	List<Stone> stones;
	int whitePrisoners;
	int blackPrisoners;
	int whiteTerritoryScore;
	int blackTerritoryScore;
	
	private boolean whiteToMove;
	public final int boardWidth = 19;
	
	public final int[] xAdj = {0, 1, 0, -1};
	public final int[] yAdj = {1, 0, -1, 0};
	
	char[][] territory;
	
	Stone lastPlacedStone;
	
	Set<Long> previousStates;
	
	public GoState() {
		super("goState");
		this.stones = new ArrayList<>();
		this.whitePrisoners = 0;
		this.blackPrisoners = 0;
		this.whiteTerritoryScore = 0;
		this.blackTerritoryScore = 0;
		this.whiteToMove = false;
		
		this.previousStates = new HashSet<>();
		
		this.territory = new char[boardWidth][boardWidth];
		
		for(int x = 0; x < this.boardWidth; x++) {
			for(int y = 0; y < this.boardWidth; y++) {
				this.territory[x][y] = ' ';
			}
		}
		
		this.lastPlacedStone = null;
	}
	
	public GoState(GoState other) {
		super("goState");
		this.stones = getDeepCopy(other.stones);
		this.whitePrisoners = other.whitePrisoners;
		this.blackPrisoners = other.blackPrisoners;
		this.whiteTerritoryScore = other.whiteTerritoryScore;
		this.blackTerritoryScore = other.blackTerritoryScore;
		this.whiteToMove = other.whiteToMove;
		
		this.previousStates = new HashSet<>(other.previousStates);
		
		this.territory = new char[boardWidth][boardWidth];
		
		for(int x = 0; x < this.boardWidth; x++) {
			for(int y = 0; y < this.boardWidth; y++) {
				this.territory[x][y] = other.territory[x][y];
			}
		}
		
		this.lastPlacedStone = other.lastPlacedStone == null ? null : other.lastPlacedStone.deepCopy();
	}

	// todo: guarantee no hash collisions
	// i.e. do this smarter
	public long boardStateHash() {
		this.stones.sort(new StoneComparator());
		long hash = 7;
		hash = 41 * hash + Objects.hashCode(this.stones);
		hash = 41 * hash + (this.whiteToMove ? 1 : 0);
		return hash;
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

	public int getWhitePrisoners() {
		return whitePrisoners;
	}

	public int getBlackPrisoners() {
		return blackPrisoners;
	}

	public int getWhiteTerritoryScore() {
		return whiteTerritoryScore;
	}

	public int getBlackTerritoryScore() {
		return blackTerritoryScore;
	}

	public Stone getLastPlacedStone() {
		return lastPlacedStone;
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
		
		// check for liberties
		if(!willStoneSurvive(x, y, movingColor)) {
			return false;
		}
		
		return true;
	}	
	public boolean isInBounds(int x, int y) {
		return(x >= 0 && x < this.boardWidth && y >= 0 && y < this.boardWidth);
	}

	public void placeStone(int x, int y, Stone.Color movingColor) {
		Stone newStone = new Stone(x, y, movingColor);
		this.lastPlacedStone = newStone;
		stones.add(newStone);
	}
	
	List<Stone> getNeighbors(int x, int y) {
		List<Stone> retval = new ArrayList<>();
		for(int i = 0; i < 4; i++) {
			
			// iterate adjacent positions
			int neighborX = x + this.xAdj[i];
			int neighborY = y + this.yAdj[i];
			retval.add(getStoneAt(neighborX, neighborY));
		}
		return retval;
	}
	
	boolean willStoneSurvive(int x, int y, Stone.Color color) {
		
		List<Stone> neighbors = getNeighbors(x, y);
		for(Stone neighbor: neighbors) {
			if(neighbor != null && neighbor.getColor() != color && neighbor.getLiberties() == 1) {
				// we're about to remove this stone, so we'll have at least one liberty
				return true;
			}
		}
		
		for(Stone neighbor: neighbors) {
			if(neighbor == null) {
				// we'll have at least one liberty, so it's legal
				return true;
			}
		}

		return false;
	}
	
	void removeIfLastLibertyTaken(int x, int y, Stone.Color color) {
		// we're about to place a stone at (x,y), so check its neighbors
		// and remove them if they're about to lose their last liberty
		List<Stone> neighbors = getNeighbors(x, y);
		for(Stone neighbor: neighbors) {
			if(neighbor != null && neighbor.getColor() != color && neighbor.getLiberties() == 1) {
				// we can remove this stone
				removeStoneGroup(neighbor);
			}
		}
	}
	
	void removeStoneGroup(Stone stone) {
		
		// remove it and add it to the other player's prisoner count
		this.stones.remove(stone);
		if(stone.getColor() == Stone.Color.WHITE) {
			this.blackPrisoners++;
		} else {
			this.whitePrisoners++;
		}
		
		// recursively remove the rest of the group
		List<Stone> neighbors = getNeighbors(stone.getX(), stone.getY());
		for(Stone neighbor: neighbors) {
			if(neighbor != null && neighbor.getColor() == stone.getColor()) {
				removeStoneGroup(neighbor);
			}
		}
	}

	public void nextMove() {
		this.stones.sort(new StoneComparator());
		this.previousStates.add(boardStateHash());
		
		Object[] psa = this.previousStates.toArray();
		for(int i=0; i<psa.length; i++) {
			logger.info("board state {}: {}", i, psa[i]);
		}
		
		this.whiteToMove = !this.whiteToMove;
	}

	void calculateLiberties() {
		for(Stone stone: this.stones) {
			stone.calculateLiberties(this);
		}
	}
	
	boolean containsPreviousState(GoState other) {
		return this.previousStates.contains(other.boardStateHash());
		/*
		long otherHash = other.boardStateHash();
		for(long prevState: this.previousStates) {
			if(prevState == otherHash) {
				return true;
			}
		}
		return false;
		*/
	}
	
	void calculateTerritory() {
		
		this.whiteTerritoryScore = 0;
		this.blackTerritoryScore = 0;
		
		for(int x = 0; x < this.boardWidth; x++) {
			for(int y = 0; y < this.boardWidth; y++) {
				
				Set<Stone.Color> borders = new HashSet<>();
				Set<String> squaresChecked = new HashSet<>();
				
				calculateTerritory(x, y, squaresChecked, borders);
				
				if(borders.size() == 1) {
					if(borders.contains(Stone.Color.WHITE)) {
						territory[x][y] = 'w';
						this.whiteTerritoryScore++;
					} else {
						territory[x][y] = 'b';
						this.blackTerritoryScore++;
					}
				} else {
					territory[x][y] = ' ';
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
			
			// iterate adjacent positions
			int nextX = x + this.xAdj[i];
			int nextY = y + this.yAdj[i];

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
	
	List<Stone> getDeepCopy(List<Stone> otherStones) {
		List<Stone> retval = new ArrayList<>();
		for(Stone s: otherStones) {
			if(s != null) {
				retval.add(s.deepCopy());
			}
		}
		return retval;
	}
}
