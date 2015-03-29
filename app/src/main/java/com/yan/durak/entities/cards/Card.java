package com.yan.durak.entities.cards;

/**
 * Created by Yan-Home on 12/14/2014.
 */
public class Card {

    public static class Suit {
        public static final String CLUBS = "clubs";
        public static final String DIAMONDS = "diamonds";
        public static final String HEARTS = "hearts";
        public static final String SPADES = "spades";
    }

    public static class Rank {
        public static final String SIX = "six";
        public static final String SEVEN = "seven";
        public static final String EIGHT = "eight";
        public static final String NINE = "nine";
        public static final String TEN = "ten";
        public static final String JACK = "jack";
        public static final String QUEEN = "queen";
        public static final String KING = "king";
        public static final String ACE = "ace";
    }

    private String mRank;
    private String mSuit;

    public Card(String rank, String suit) {
        mRank = rank;
        mSuit = suit;
    }

    public String getRank() {
        return mRank;
    }

    public String getSuit() {
        return mSuit;
    }

    @Override
    public String toString() {
        return "Card{" +
                "mRank='" + mRank + '\'' +
                ", mSuit='" + mSuit + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card)) return false;

        Card card = (Card) o;

        if (!mRank.equals(card.mRank)) return false;
        if (!mSuit.equals(card.mSuit)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = mRank.hashCode();
        result = 31 * result + mSuit.hashCode();
        return result;
    }
}
