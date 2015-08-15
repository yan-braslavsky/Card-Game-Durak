package com.yan.durak.gamelogic.commands.core;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.exceptions.EmptyPileException;

import java.util.List;

/**
 * Created by Yan-Home on 12/21/2014.
 */
public class SelectTrumpCommand extends BaseSessionCommand {

    private int mTrumpPileIndex;

    @Override
    public void execute() {
        List<Card> pile = getGameSession().getPilesStack().get(mTrumpPileIndex).getCardsInPile();
        if (pile.isEmpty()) {
            throw new EmptyPileException(mTrumpPileIndex);
        }

        //set the suit of the bottom card (which is a first card)
        String suit = pile.get(0).getSuit();
        getGameSession().setTrumpSuit(suit);
    }

    public void setTrumpPileIndex(int trumpPileIndex) {
        mTrumpPileIndex = trumpPileIndex;
    }
}
