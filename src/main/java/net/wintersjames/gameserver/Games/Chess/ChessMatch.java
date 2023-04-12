package net.wintersjames.gameserver.Games.Chess;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import net.wintersjames.gameserver.Games.GameMatch;

/**
 *
 * @author james
 */
public class ChessMatch extends GameMatch {
    
	final private int whitePlayer;
	final private int blackPlayer;
	
    public ChessMatch(long id, int whitePlayer, int blackPlayer) {
        super(id, Chess.class, new ChessState());
		this.whitePlayer = whitePlayer;
		this.blackPlayer = blackPlayer;
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
		
		// get the color of the moving player
		boolean isWhite = (uid == whitePlayer);
		
		ChessState state = (ChessState)this.gameState;
		
		// check if move is valid
		if(!state.canMove(fromPos, toPos, isWhite)) {
			return false;
		}
		
		state.captureAt(toPos);
		state.move(fromPos, toPos);
		
		state.nextMove();
		
		
		return true;
	}
    
	@Override
	public Map<String, String> getAttributes(int uid) {
		Map<String, String> retval = new HashMap<>();
		retval.put("playerColor", uid == whitePlayer ? "white" : "black");
		return retval;
	}
}
