package net.wintersjames.gameserver.Games.Chess;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
	public boolean handleMove(int uid, HttpServletRequest request) {
		
		String fromPos = URLDecoder.decode(
                request.getParameter("from"),  
                StandardCharsets.UTF_8);
		String toPos = URLDecoder.decode(
                request.getParameter("to"),  
                StandardCharsets.UTF_8);
		
		System.out.println("moving " + fromPos + " -> " + toPos);
		
		((ChessState)this.gameState).captureAt(toPos);
		((ChessState)this.gameState).move(fromPos, toPos);
		
		return true;
	}
    
}
