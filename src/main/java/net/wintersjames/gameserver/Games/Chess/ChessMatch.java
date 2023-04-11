package net.wintersjames.gameserver.Games.Chess;

import jakarta.servlet.http.HttpServletRequest;
import net.wintersjames.gameserver.Games.GameMatch;

/**
 *
 * @author james
 */
public class ChessMatch extends GameMatch {
    
    public ChessMatch(long id) {
        super(id, Chess.class, new ChessState());
    }

	@Override
	public String handleMove(int uid, HttpServletRequest request) {
		System.out.println("ChessMatch handle move for user " + Integer.toString(uid));
		return "success";
	}
    
}
