package com.yan.durak.models;

import com.yan.durak.gamelogic.cards.Card;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by ybra on 20/04/15.
 */
public class PileModel {

    private final List<Card> mCardsInPile;
    private final List<Card> mUnmodifiableCardsCollection;
    private int mPileIndex;

    public PileModel(int pileIndex) {
        this.mCardsInPile = new ArrayList<>();
        this.mUnmodifiableCardsCollection = Collections.unmodifiableList(mCardsInPile);
        this.mPileIndex = pileIndex;
    }

    /**
     * Returns a card with provided rank and suit if it is in the pile
     *
     * @return card if it is in the pile or null otherwise
     */
    public Card findCardByRankAndSuit(String rank, String suit) {
        for (Card card : mCardsInPile) {
            if (card.getRank().equals(rank) && card.getSuit().equals(suit))
                return card;
        }

        return null;
    }

    public void addCard(Card card) {

        //do not add the same card twice
        if(mCardsInPile.contains(card))
            return;

        mCardsInPile.add(card);
    }

    public void removeCard(Card movedCard) {
        mCardsInPile.remove(movedCard);
    }

    public int getPileIndex() {
        return mPileIndex;
    }

    public void setPileIndex(int pileIndex) {
        mPileIndex = pileIndex;
    }

    public List<Card> getCardsInPile() {
        return mUnmodifiableCardsCollection;
    }

    public boolean isCardInPile(Card card) {
        return mCardsInPile.contains(card);
    }

    public void addCardAtIndex(Card card, int indexOfCard) {
        //do not add the same card twice
        if(mCardsInPile.contains(card))
            return;

        mCardsInPile.add(indexOfCard,card);
    }
}
