package com.yan.durak.gamelogic.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ybra on 19.12.2014.
 * <p/>
 * Defines a collection of cards managed inside a single pile.
 */
public class Pile {

    private List<Card> mCardsInPile;

    private ArrayList<String> mTags;

    public List<String> getTags() {
        return Collections.unmodifiableList(mTags);
    }

    public Pile() {
        mTags = new ArrayList<>();
        mCardsInPile = new ArrayList<>(CardsHelper.MAX_CARDS_IN_DECK);
    }

    public void addTag(final String tag) {
        mTags.add(tag);
    }

    public void removeTag(final String tag) {
        mTags.remove(tag);
    }

    public void addCardToPile(final Card card) {
        mCardsInPile.add(card);
    }

    public void removeCardFromPile(final Card card) {
        mCardsInPile.remove(card);
    }

    public List<Card> getCardsInPile() {
        return mCardsInPile;
    }

    public boolean hasTag(final String tag) {
        return mTags.contains(tag);
    }

    public static class PileTags {
        public static final String PLAYER_PILE_TAG = "PLAYER_PILE_TAG";
        public static final String STOCK_PILE_TAG = "STOCK_PILE_TAG";
        public static final String DISCARD_PILE_TAG = "DISCARD_PILE_TAG";
        public static final String FIELD_PILE = "FIELD_PILE";
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < mTags.size(); i++) {
            final String tag = mTags.get(i);
            str += tag;
        }
        return str;
    }
}
