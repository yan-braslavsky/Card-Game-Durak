package com.yan.durak.gamelogic.player;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.communication.protocol.data.PlayerMetaData;

import java.util.Collection;
import java.util.List;

/**
 * Created by Yan-Home on 12/21/2014.
 */
public interface IPlayer {

    /**
     * Player requested to give a card that he would like
     * to attack with.
     */
    Card getCardForAttack();

    /**
     * Index of the player in game
     */
    int getGameIndex();

    /**
     * Index of pile in game associated with current player
     */
    int getPileIndex();

    /**
     * Player must cover each pile and return them back.
     * If not all piles covered , means player taking them.
     */
    List<Pile> retaliatePiles(List<Pile> pilesToRetaliate);

    /**
     * Player must choose what cards will he throw in into
     * the field for other player to retaliate.
     *
     * @param allowedRanksToThrowIn         all the ranks that can be thrown in
     * @param allowedAmountOfCardsToThrowIn the amount of cards that can be thrown in
     * @return cards that player decided to throw in
     */
    List<Card> getThrowInCards(Collection<String> allowedRanksToThrowIn, int allowedAmountOfCardsToThrowIn);

    /**
     * Contains data about the player account etc...
     */
    PlayerMetaData getPlayerMetaData();
}
