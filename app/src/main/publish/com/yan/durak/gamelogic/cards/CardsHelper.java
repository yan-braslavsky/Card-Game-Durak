package com.yan.durak.gamelogic.cards;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by ybra on 19.12.2014.
 * <p/>
 * Helper class that does bunch of common operations on cards.
 */
public class CardsHelper {

    public static final int MAX_CARDS_IN_DECK = 36;

    private static List<String> ORDERED_36_DECK_RANKS = Arrays.asList(new String[]{
            Card.Rank.SIX,
            Card.Rank.SEVEN,
            Card.Rank.EIGHT,
            Card.Rank.NINE,
            Card.Rank.TEN,
            Card.Rank.JACK,
            Card.Rank.QUEEN,
            Card.Rank.KING,
            Card.Rank.ACE
    });

    /**
     * Compares 2 cards.
     *
     * @param leftCard
     * @param rightCard
     * @param trumpSuite
     * @return positive integer if right card bigger than left , negative integer otherwise. zero if cards are not comparable.
     */
    public static int compareCards(final Card leftCard, final Card rightCard, final String trumpSuite) {

        //suits are equal case
        if (leftCard.getSuit().equals(rightCard.getSuit())) {
            final int card1Index = ORDERED_36_DECK_RANKS.indexOf(leftCard.getRank());
            final int card2Index = ORDERED_36_DECK_RANKS.indexOf(rightCard.getRank());
            return card2Index - card1Index;
        }
        //suits are not equal , but left one is trump
        else if (leftCard.getSuit().equals(trumpSuite)) {
            return -1;
        }
        //suits are not equal , but right one is trump
        else if (rightCard.getSuit().equals(trumpSuite)) {
            return 1;
        }
        //none of cards is a trump , just compare by rank
        else {
            return 0;
        }
    }

    public static ArrayList<Card> create36Deck() {
        final ArrayList<Card> deck = new ArrayList<>(36);
        deck.addAll(createSuit(Card.Suit.CLUBS));
        deck.addAll(createSuit(Card.Suit.DIAMONDS));
        deck.addAll(createSuit(Card.Suit.HEARTS));
        deck.addAll(createSuit(Card.Suit.SPADES));
        return deck;
    }

    private static Collection<? extends Card> createSuit(final String suit) {
        final ArrayList<Card> suitCards = new ArrayList<>();
        suitCards.add(new Card(Card.Rank.SIX, suit));
        suitCards.add(new Card(Card.Rank.SEVEN, suit));
        suitCards.add(new Card(Card.Rank.EIGHT, suit));
        suitCards.add(new Card(Card.Rank.NINE, suit));
        suitCards.add(new Card(Card.Rank.TEN, suit));
        suitCards.add(new Card(Card.Rank.JACK, suit));
        suitCards.add(new Card(Card.Rank.QUEEN, suit));
        suitCards.add(new Card(Card.Rank.KING, suit));
        suitCards.add(new Card(Card.Rank.ACE, suit));
        return suitCards;
    }

    public static List<Card> createNumberOfCards(final int cardsAmount) {
        final ArrayList<Card> deck = create36Deck();
        return deck.subList(0, cardsAmount);
    }

    /**
     * Returns list of cards filtered by suit.
     * Doesn't copy the cards.
     *
     * @param pile to filter
     * @param suit to filter by
     * @return list of cards
     */
    public static List<Card> filterCardsBySuit(final Pile pile, final String suit) {
        final ArrayList<Card> filteredCards = new ArrayList<>();
        for (int i = 0; i < pile.getCardsInPile().size(); i++) {
            final Card card = pile.getCardsInPile().get(i);
            if (card.getSuit().equals(suit)) {
                filteredCards.add(card);
            }
        }
        return filteredCards;
    }

    /**
     * Does not modify parameters.
     *
     * @param ranks collection of ranks
     * @param cards collection of cards to be checked
     * @return true if one or more ranks found among the provided collection of cards
     */
    public static boolean isOneOfTheRanksInPile(final Collection<String> ranks, final List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            final Card cardInPlayerPile = cards.get(i);
            for (final String allowedRank : ranks) {
                if (cardInPlayerPile.getRank().equals(allowedRank)) {
                    return true;
                }
            }
        }
        return false;
    }
}
