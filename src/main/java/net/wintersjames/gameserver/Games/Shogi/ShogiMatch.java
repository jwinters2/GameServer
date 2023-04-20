package net.wintersjames.gameserver.Games.Shogi;

import jakarta.servlet.http.HttpServletRequest;
import net.wintersjames.gameserver.Games.Chess.Chess;
import net.wintersjames.gameserver.Games.Chess.ChessState;
import net.wintersjames.gameserver.Games.GameMatch;
import net.wintersjames.gameserver.Games.GameState;

/**
 *
 * @author james
 */
public class ShogiMatch extends GameMatch {

	public ShogiMatch(long id, int whitePlayer, int blackPlayer) {
        super(id, Chess.class, new ChessState());
    }

	@Override
	public boolean handleMove(int uid, HttpServletRequest request) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
