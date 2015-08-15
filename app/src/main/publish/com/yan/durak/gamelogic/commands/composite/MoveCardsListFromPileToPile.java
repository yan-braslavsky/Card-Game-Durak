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

    public void setCardList(List<Card> cardList) {

        //copy the card list
        mCardList = new ArrayList<>(cardList);
    }

    public void setFromPileIndex(int fromPileIndex) {
        mFromPileIndex = fromPileIndex;
    }

    public void setToPileIndex(int toPileIndex) {
        mToPileIndex = toPileIndex;
    }

    @Override
    public void execute() {

        MoveCardFromPileToPileCommand moveCardFromPileToPileCommand = new MoveCardFromPileToPileCommand();
        moveCardFromPileToPileCommand.setFromPileIndex(mFromPileIndex);
        moveCardFromPileToPileCommand.setToPileIndex(mToPileIndex);

        for (Card card : mCardList) {
            moveCardFromPileToPileCommand.setCardToMove(card);
            getGameSession().executeCommand(moveCardFromPileToPileCommand);
        }

    }
}
