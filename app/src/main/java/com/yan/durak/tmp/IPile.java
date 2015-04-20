package com.yan.durak.tmp;

import com.yan.durak.gamelogic.cards.Card;

/**
 * Created by ybra on 20/04/15.
 */
public interface IPile {

    Card getCardByRankAndSuit(String rank, String suit);

    void addCard(Card card);

    void removeCard(Card movedCard);
}
