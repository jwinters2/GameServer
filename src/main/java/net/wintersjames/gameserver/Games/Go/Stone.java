package net.wintersjames.gameserver.Games.Go;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author james
 */
public class Stone implements Serializable {
	
	Logger logger = LoggerFactory.getLogger(Stone.class);

	public enum Color {
		BLACK,
		WHITE
	}
	
	int x;
	int y;
	Color color;
	int liberties;

	public Stone(int x, int y, Color color) {
		this.x = x;
		this.y = y;
		this.color = color;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public Color getColor() {
		return color;
	}
	
	public int getLiberties() {
		return liberties;
	}
	
	public void calculateLiberties(GoState state) {
		Set<Stone> stones = new HashSet<>();
		Set<String> libertySet = new HashSet<>();
		
		calculateLiberties(state, stones, libertySet);
		
		this.liberties = libertySet.size();
	}
	
	private void calculateLiberties(GoState state, Set<Stone> stones, Set<String> liberties) {
		//logger.info("begin calculate liberties for stone {},{}", x, y);
		if(stones.contains(this)) {
			return;
		}
		stones.add(this);
		
		for(int i = 0; i < 4; i++) {
			/*
				  0
				3 . 1
				  2
			*/
			int nextX = this.x + (i == 3 ? 1 : (i == 1 ? -1 : 0));
			int nextY = this.y + (i == 2 ? 1 : (i == 0 ? -1 : 0));

			if(state.isInBounds(nextX, nextY)) {

				Stone newStone = state.getStoneAt(nextX, nextY);

				if(newStone != null) {
					//logger.info("recursive newStone: {},{}", nextX, nextY);
					if(newStone.getColor() == this.color) {
						newStone.calculateLiberties(state, stones, liberties);
					}
				} else {
					//logger.info("new liberty for stone: {},{}", nextX, nextY);
					liberties.add(Integer.toString(nextX) + "," + Integer.toString(nextY));
				}

			}
		}
	}
	
	public Stone deepCopy() {
		Stone retval = new Stone(this.x, this.y, this.color);
		retval.liberties = this.liberties;
		return retval;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 59 * hash + this.x;
		hash = 59 * hash + this.y;
		hash = 59 * hash + Objects.hashCode(this.color);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Stone other = (Stone) obj;
		if (this.x != other.x) {
			return false;
		}
		if (this.y != other.y) {
			return false;
		}
		return this.color == other.color;
	}
	
	

}
