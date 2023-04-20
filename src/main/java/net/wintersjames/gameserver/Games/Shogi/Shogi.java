package net.wintersjames.gameserver.Games.Shogi;


import net.wintersjames.gameserver.Games.Game;


/**
 *
 * @author james
 */
public class Shogi extends Game {
	public Shogi() {
        this.description = "Japanese chess.";
        this.image = "/gameserver/images/shogi.jpg";
        this.title = "Shogi";
        this.isSinglePlayer = false;
        this.isMultiPlayer = true;
    }
}
