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
