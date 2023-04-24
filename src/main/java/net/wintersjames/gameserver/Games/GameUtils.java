package net.wintersjames.gameserver.Games;

import net.wintersjames.gameserver.Games.Chess.Chess;
import net.wintersjames.gameserver.Games.Chess.ChessMatch;
import net.wintersjames.gameserver.Games.Go.Go;
import net.wintersjames.gameserver.Games.Go.GoMatch;
import net.wintersjames.gameserver.Games.Shogi.Shogi;
import net.wintersjames.gameserver.Games.Shogi.ShogiMatch;
import net.wintersjames.gameserver.Queue.GameInvite;

/**
 *
 * @author james
 */
public class GameUtils {

	static GameMatch newMatch(GameInvite invite) {
	
		 return switch (invite.getGameStr().toLowerCase()) {
            case "chess"	-> new ChessMatch(invite.getTimestamp(), invite.getToUid(), invite.getFromUid());
			case "shogi"	-> new ShogiMatch(invite.getTimestamp(), invite.getToUid(), invite.getFromUid());
			case "go"		-> new GoMatch(invite.getTimestamp(), invite.getToUid(), invite.getFromUid());
            default -> null;
        };
		 
	}
    
    private GameUtils() {}
    
    public static Class getClassFromName(String name) {
        return switch (name) {
            case "Chess", "chess"	-> Chess.class;
			case "Shogi", "shogi"	-> Shogi.class;
			case "Go", "go"			-> Go.class;
            default -> null;
        };
    }
    
}
