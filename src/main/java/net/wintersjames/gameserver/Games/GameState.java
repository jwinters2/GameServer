/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games;

/**
 *
 * @author james
 */
public interface GameState {
    public enum Result {
        PLAYER_1_WINS,
        PLAYER_2_WWINS,
        DRAW,
        INCOMPLETE,
    }
}
