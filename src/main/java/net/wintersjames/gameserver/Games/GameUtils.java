package net.wintersjames.gameserver.Games;

import net.wintersjames.gameserver.Games.Chess.Chess;
import net.wintersjames.gameserver.Games.Chess.ChessMatch;
import net.wintersjames.gameserver.Games.Go.Go;
import net.wintersjames.gameserver.Games.Go.GoMatch;
import net.wintersjames.gameserver.Games.Shogi.Shogi;
import net.wintersjames.gameserver.Games.Shogi.ShogiMatch;
import net.wintersjames.gameserver.Games.Shogi.Variants.ChuShogi;
import net.wintersjames.gameserver.Queue.GameInvite;

/**
 *
 * @author james
 */
public class GameUtils {
	
	private GameUtils() {}

	static GameMatch newMatch(GameInvite invite) {
	
		if(invite == null) {
			return null;
		}
		
		 return switch (invite.getGameStr().toLowerCase()) {
            case "chess"	-> new ChessMatch(invite.getTimestamp(), invite.getToUid(), invite.getFromUid());
			case "shogi"	-> new ShogiMatch(invite.getTimestamp(), invite.getToUid(), invite.getFromUid());
			case "go"		-> new GoMatch(invite.getTimestamp(), invite.getToUid(), invite.getFromUid());
			case "chu_shogi", "chushogi"	
							-> new ShogiMatch(invite.getTimestamp(), invite.getToUid(), invite.getFromUid(), ChuShogi.class, "chushogi");
            default -> null;
        };
		 
	}
    
    public static Class getClassFromName(String name) {
		
		if(name == null) {
			return null;
		}
		
        return switch (name.toLowerCase()) {
            case "chess"	-> Chess.class;
			case "shogi"	-> Shogi.class;
			case "go"		-> Go.class;
			case "chu_shogi", "chushogi"	
							-> ChuShogi.class;
            default -> null;
        };
    }
	
	public static String getDisplayNameFromName(String name) {
		return switch (name.toLowerCase()) {
            case "chess"	-> "Chess";
			case "shogi"	-> "Shogi";
			case "go"		-> "Go";
			case "chu_shogi", "chushogi"	
							-> "Chu Shogi";
            default -> name;
        };
	}
	
    
}
