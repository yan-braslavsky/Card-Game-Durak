package com.yan.durak.gamelogic.commands.composite;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;

import java.util.List;

/**
 * Created by Yan-Home on 12/21/2014.
 * <p/>
 * We want pile to have certain amount of cards. If it has less , we pulling
 * additional cards from another pile.
 */
public class CompletePileToAmountOfCards extends BaseSessionCommand {

    private int mFromPileIndex;
    private int mToPileIndex;
    private int mCardsAmount;

    @Override
    public void execute() {

        List<Card> cardsInPile = getGameSession().getPilesStack().get(mToPileIndex).getCardsInPile();
        if (cardsInPile.size() < mCardsAmount) {

            int cardsToAdd = mCardsAmount - cardsInPile.size();

            MoveTopCardsFromPileToPile moveTopCardsFromPileToPile = new MoveTopCardsFromPileToPile();
            moveTopCardsFromPileToPile.setFromPileIndex(mFromPileIndex);
            moveTopCardsFromPileToPile.setToPileIndex(mToPileIndex);
            moveTopCardsFromPileToPile.setCardsAmount(cardsToAdd);
            getGameSession().executeCommand(moveTopCardsFromPileToPile);
        }

    }

    public void setFromPileIndex(int fromPileIndex) {
        mFromPileIndex = fromPileIndex;
    }

    public void setToPileIndex(int toPileIndex) {
        mToPileIndex = toPileIndex;
    }

    public void setCardsAmount(int cardAmount) {
        mCardsAmount = cardAmount;
    }
}
