package com.yan.durak.gamelogic.game;

/**
 * Created by Yan-Home on 12/23/2014.
 */
public interface IGameRules {

    /**
     * Game is limited to 3 players
     */
    int MAX_PLAYERS_IN_GAME = 3;

    /**
     * Game should have minimum 2 players
     */
    int MIN_PLAYERS_IN_GAME = 2;

    /**
     * Defines the minimum amount of cards that player should have at the
     * beginning of each turn as long as stock pile is not empty
     */
    int AMOUNT_OF_CARDS_IN_PLAYER_HANDS = 6;

    /**
     * Defines the maximum amount of cards that player can
     * retaliate against.
     */
    int MAX_PILES_ON_FIELD_AMOUNT = 6;

    /**
     * Returns amount of players in current game
     */
    int getTotalPlayersInGameAmount();

}