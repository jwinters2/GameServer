package net.wintersjames.gameserver.Games.Go;

import java.util.Comparator;

/**
 *
 * @author james
 */
public class StoneComparator implements Comparator<Stone> {

	@Override
	public int compare(Stone t1, Stone t2) {
		if(Integer.compare(t1.x, t2.x) != 0) {
			return Integer.compare(t1.x, t2.x);
		}
		
		if(Integer.compare(t1.y, t2.y) != 0) {
			return Integer.compare(t1.y, t2.y);
		}
		
		return 0;
	}
	
}
