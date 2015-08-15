package com.yan.durak.gamelogic.validation;


import com.yan.durak.gamelogic.game.GameSession;
import com.yan.durak.gamelogic.game.IGameRules;

/**
 * Created by Yan-Home on 1/22/2015.
 */
public class GameSessionValidations {

    /**
     * Validates that given pile index exists in provided game session
     *
     * @param pileIndex   index of pile to validate
     * @param gameSession game session to validate pile index in
     * @return true if pile exists , false otherwise
     */
    public static boolean validatePilesExist(final int pileIndex, final GameSession gameSession) {
        return (pileIndex < 0 || pileIndex >= gameSession.getPilesStack().size()) ? false : true;
    }

    /**
     * Checks the amount of players in game for validity , and throws
     * exceptions in case the amount is not valid
     *
     * @param playersInGame the current requested amount of players in the game
     */
    public static void validateAmountOfPlayersInGame(final int playersInGame, final IGameRules gameRules) {
        if (playersInGame > IGameRules.MAX_PLAYERS_IN_GAME)
            throw new RuntimeException("Can't play with " + playersInGame + "Game is limited to " + IGameRules.MAX_PLAYERS_IN_GAME + " players");
        else if (playersInGame < IGameRules.MIN_PLAYERS_IN_GAME)
            throw new RuntimeException("Can't play with " + playersInGame + "Game should have at least " + IGameRules.MAX_PLAYERS_IN_GAME + " players");
        else if (playersInGame != gameRules.getTotalPlayersInGameAmount())
            throw new RuntimeException("Can't play with " + playersInGame + "According to game rules , game should have exactly " + gameRules.getTotalPlayersInGameAmount() + " players");
    }

}
