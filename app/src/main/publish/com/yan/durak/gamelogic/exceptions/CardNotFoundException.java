package com.yan.durak.gamelogic.exceptions;


import com.yan.durak.gamelogic.cards.Card;

/**
 * Created by Yan-Home on 12/21/2014.
 */
public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException(Card card, int pileIndex) {
        super("the card " + card + " is not found in pile index : " + pileIndex);
    }
}