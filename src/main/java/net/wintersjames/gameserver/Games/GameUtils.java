/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.wintersjames.gameserver.Games;

import net.wintersjames.gameserver.Games.Chess.Chess;
import net.wintersjames.gameserver.Games.Shogi.Shogi;

/**
 *
 * @author james
 */
public class GameUtils {
    
    private GameUtils() {}
    
    public static Class getClassFromName(String name) {
        return switch (name) {
            case "Chess", "chess" -> Chess.class;
			case "Shogi", "shogi" -> Shogi.class;
            default -> null;
        };
    }
    
}
