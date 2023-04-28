package net.wintersjames.gameserver.Games.Shogi.Variants;

import net.wintersjames.gameserver.Games.Game;

/**
 *
 * @author james
 */
public class ChuShogi extends Game {
	public ChuShogi() {
        this.description = "\"Middle\" Japanese chess played on a 12x12 board.";
        this.image = "/gameserver/images/chushogi.jpg";
        this.title = "Chu Shogi";
        this.isSinglePlayer = false;
        this.isMultiPlayer = true;
    }
}
