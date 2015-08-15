package com.yan.durak.gamelogic.commands.composite;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.commands.core.MoveCardFromPileToPileCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ybra on 19.12.2014.
 */
public class MoveCardsListFromPileToPile extends BaseSessionCommand {

    //must contain exact instances of cards to be moved
    private List<Card> mCardList;
    private int mFromPileIndex;
    private int mToPileIndex;

    public void setCardList(final List<Card> cardList) {

        //copy the card list
        mCardList = new ArrayList<>(cardList);
    }

    public void setFromPileIndex(final int fromPileIndex) {
        mFromPileIndex = fromPileIndex;
    }

    public void setToPileIndex(final int toPileIndex) {
        mToPileIndex = toPileIndex;
    }

    @Override
    public void execute() {

        final MoveCardFromPileToPileCommand moveCardFromPileToPileCommand = new MoveCardFromPileToPileCommand();
        moveCardFromPileToPileCommand.setFromPileIndex(mFromPileIndex);
        moveCardFromPileToPileCommand.setToPileIndex(mToPileIndex);

        for (final Card card : mCardList) {
            moveCardFromPileToPileCommand.setCardToMove(card);
            getGameSession().executeCommand(moveCardFromPileToPileCommand);
        }

    }
}
