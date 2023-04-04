/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games;

/**
 *
 * @author james
 */
public interface Playable {
    
    public GameState getGameState();
    
    public boolean move(GameMove gameMove);
    
}
