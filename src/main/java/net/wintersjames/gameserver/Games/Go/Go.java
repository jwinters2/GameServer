package net.wintersjames.gameserver.Games.Go;

import net.wintersjames.gameserver.Games.Game;

/**
 *
 * @author james
 */
public class Go extends Game {
    
    public Go() {
        this.description = "A game about placing stones to claim territory.";
        this.image = "/gameserver/images/go.jpg";
        this.title = "Go";
        this.isSinglePlayer = false;
        this.isMultiPlayer = true;
    }
    
}