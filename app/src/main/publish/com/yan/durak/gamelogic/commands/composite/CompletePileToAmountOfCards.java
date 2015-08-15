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

        final List<Card> cardsInPile = getGameSession().getPilesStack().get(mToPileIndex).getCardsInPile();
        if (cardsInPile.size() < mCardsAmount) {

            final int cardsToAdd = mCardsAmount - cardsInPile.size();

            final MoveTopCardsFromPileToPile moveTopCardsFromPileToPile = new MoveTopCardsFromPileToPile();
            moveTopCardsFromPileToPile.setFromPileIndex(mFromPileIndex);
            moveTopCardsFromPileToPile.setToPileIndex(mToPileIndex);
            moveTopCardsFromPileToPile.setCardsAmount(cardsToAdd);
            getGameSession().executeCommand(moveTopCardsFromPileToPile);
        }

    }

    public void setFromPileIndex(final int fromPileIndex) {
        mFromPileIndex = fromPileIndex;
    }

    public void setToPileIndex(final int toPileIndex) {
        mToPileIndex = toPileIndex;
    }

    public void setCardsAmount(final int cardAmount) {
        mCardsAmount = cardAmount;
    }
}
