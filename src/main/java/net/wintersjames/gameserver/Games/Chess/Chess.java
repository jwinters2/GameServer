/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games.Chess;

import net.wintersjames.gameserver.Games.Game;

/**
 *
 * @author james
 */
public class Chess extends Game {
    
    public Chess() {
        this.description = "Basic chess that everyone probably already knows.";
        this.image = "/chess.jpg";
        this.title = "Chess";
        this.isSinglePlayer = false;
        this.isMultiPlayer = true;
    }
    
}
