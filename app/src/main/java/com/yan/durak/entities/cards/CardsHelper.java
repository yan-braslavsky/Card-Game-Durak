package com.yan.durak.entities.cards;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Yan-Home on 12/14/2014.
 */
public class CardsHelper {

    public static ArrayList<Card> create36Deck() {
        ArrayList<Card> deck = new ArrayList<>(36);
        deck.addAll(createSuit(Card.Suit.CLUBS));
        deck.addAll(createSuit(Card.Suit.DIAMONDS));
        deck.addAll(createSuit(Card.Suit.HEARTS));
        deck.addAll(createSuit(Card.Suit.SPADES));
        return deck;
    }

    private static Collection<? extends Card> createSuit(String suit) {
        ArrayList<Card> suitCards = new ArrayList<>();
        suitCards.add(new Card(Card.Rank.SIX,suit));
        suitCards.add(new Card(Card.Rank.SEVEN,suit));
        suitCards.add(new Card(Card.Rank.EIGHT,suit));
        suitCards.add(new Card(Card.Rank.NINE,suit));
        suitCards.add(new Card(Card.Rank.TEN,suit));
        suitCards.add(new Card(Card.Rank.JACK,suit));
        suitCards.add(new Card(Card.Rank.QUEEN,suit));
        suitCards.add(new Card(Card.Rank.KING,suit));
        suitCards.add(new Card(Card.Rank.ACE,suit));
        return suitCards;
    }
}
