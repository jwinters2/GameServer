package net.wintersjames.gameserver.Games.Go;

/**
 *
 * @author james
 */
public class Stone {

	public enum Color {
		BLACK,
		WHITE
	}
	
	int x;
	int y;
	Color color;

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

}
