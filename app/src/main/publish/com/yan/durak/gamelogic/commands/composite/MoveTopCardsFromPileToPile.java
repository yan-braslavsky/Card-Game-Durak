package com.yan.durak.gamelogic.commands.composite;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ybra on 19.12.2014.
 */
public class MoveTopCardsFromPileToPile extends BaseSessionCommand {

    private int mFromPileIndex;
    private int mToPileIndex;
    private int mCardsAmount;

    public void setFromPileIndex(int fromPileIndex) {
        mFromPileIndex = fromPileIndex;
    }

    public void setToPileIndex(int toPileIndex) {
        mToPileIndex = toPileIndex;
    }

    public void setCardsAmount(int cardsAmount) {
        mCardsAmount = cardsAmount;
    }

    @Override
    public void execute() {
        ArrayList<Card> cardsToMove = new ArrayList<>(mCardsAmount);
        List<Card> fromPile = getGameSession().getPilesStack().get(mFromPileIndex).getCardsInPile();

        //in case there is less cards in pile than we want to move
        int amountOfCardsToMove = Math.min(fromPile.size(), mCardsAmount);
        for (int i = 0; i < amountOfCardsToMove; i++) {
            //obtain top card
            Card card = fromPile.get(fromPile.size() - (i + 1));
            cardsToMove.add(card);
        }

        MoveCardsListFromPileToPile moveCardsListFromPileToPile = new MoveCardsListFromPileToPile();
        moveCardsListFromPileToPile.setFromPileIndex(mFromPileIndex);
        moveCardsListFromPileToPile.setToPileIndex(mToPileIndex);
        moveCardsListFromPileToPile.setCardList(cardsToMove);
        getGameSession().executeCommand(moveCardsListFromPileToPile);
    }
}
