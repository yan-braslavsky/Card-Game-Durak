package com.yan.durak.gamelogic.commands.core;


import com.yan.durak.gamelogic.cards.Card;
import com.yan.durak.gamelogic.cards.Pile;
import com.yan.durak.gamelogic.commands.BaseSessionCommand;
import com.yan.durak.gamelogic.exceptions.CardNotFoundException;
import com.yan.durak.gamelogic.exceptions.PileNotFoundException;
import com.yan.durak.gamelogic.validation.GameSessionValidations;

/**
 * Created by ybra on 19.12.2014.
 */
public class MoveCardFromPileToPileCommand extends BaseSessionCommand {

    //must be the exact instance of the card that should be moved
    private Card mCardToMove;
    private int mFromPileIndex = -1;
    private int mToPileIndex = -1;

    public void setCardToMove(Card mCardToMove) {
        this.mCardToMove = mCardToMove;
    }

    public void setFromPileIndex(int mFromPileIndex) {
        this.mFromPileIndex = mFromPileIndex;
    }

    public void setToPileIndex(int mToPileIndex) {
        this.mToPileIndex = mToPileIndex;
    }

    @Override
    public void execute() {
        validatePilesExist();


        Pile fromPile = getGameSession().getPilesStack().get(mFromPileIndex);
        Pile toPile = getGameSession().getPilesStack().get(mToPileIndex);

        for (Card card : fromPile.getCardsInPile()) {
            if (mCardToMove.equals(card)) {
                fromPile.removeCardFromPile(card);
                toPile.addCardToPile(card);
                return;
            }
        }

        throw new CardNotFoundException(mCardToMove, mFromPileIndex);
    }

    private void validatePilesExist() {
        if (!GameSessionValidations.validatePilesExist(mFromPileIndex, getGameSession()))
            throw new PileNotFoundException(mFromPileIndex);

        if (!GameSessionValidations.validatePilesExist(mToPileIndex, getGameSession()))
            throw new PileNotFoundException(mToPileIndex);
    }


    public Card getCardToMove() {
        return mCardToMove;
    }

    public int getFromPileIndex() {
        return mFromPileIndex;
    }

    public int getToPileIndex() {
        return mToPileIndex;
    }
}
