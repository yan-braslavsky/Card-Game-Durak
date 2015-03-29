package com.yan.durak.input.cards.states;

import com.yan.durak.input.cards.CardsTouchProcessor;
import com.yan.durak.input.cards.CardsTouchProcessorState;
import com.yan.durak.nodes.CardNode;
import com.yan.glengine.EngineWrapper;
import com.yan.glengine.input.YANInputManager;
import com.yan.glengine.util.geometry.YANVector2;

import java.util.ArrayList;

/**
 * Created by Yan-Home on 11/21/2014.
 * <p/>
 * In this state there only few cards enabled and intractable . Others are disabled .
 */
public class CardsTouchProcessorMultipleChoiceState extends CardsTouchProcessorState {


    public static final float FADE_OUT_DURATION = 0.3f;
    private ArrayList<CardNode> mSelectedCards;
    private CardNode mCandidateForTapCard;
    private long mLastTouchDownForTap;

    private class PreviousCardParams {
        //TODO :
    }

    public CardsTouchProcessorMultipleChoiceState(CardsTouchProcessor cardsTouchProcessor, ArrayList<CardNode> selectedCards) {
        super(cardsTouchProcessor);
        mSelectedCards = selectedCards;
    }

    @Override
    public void applyState() {

        for (CardNode cardNode : mCardsTouchProcessor.getCardNodesArray()) {
            if (mSelectedCards.contains(cardNode))
                continue;
            mCardsTouchProcessor.getCardsTweenAnimator().animateCardToAlpha(cardNode, 0.1f, FADE_OUT_DURATION);
        }
    }

    public void markCardAsChoosen(CardNode cardNode) {
        mCardsTouchProcessor.getCardsTweenAnimator().animateCardToAlpha(cardNode, 0.1f, FADE_OUT_DURATION);
        mSelectedCards.remove(cardNode);
    }

    @Override
    public boolean onTouchUp(float normalizedX, float normalizedY) {

        //if touch down not on the selected card , go back to  default state , else go to drag state
        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());

        //find touched card under the touch point
        CardNode touchedCard = mCardsTouchProcessor.findTouchedCard(touchToWorldPoint);

        if (touchedCard != mCandidateForTapCard) {
            mCandidateForTapCard = null;
            return true;
        }

        if ((System.currentTimeMillis() - mLastTouchDownForTap) < 65) {
            return true;
        }

        for (CardNode selectedCard : mSelectedCards) {
            if (touchedCard == selectedCard) {
                if (mCardsTouchProcessor.getCardsTouchProcessorListener() != null) {
                    mCardsTouchProcessor.getCardsTouchProcessorListener().onSelectedCardTap(selectedCard);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onTouchDrag(float normalizedX, float normalizedY) {
        //disable drag for this state
        return false;
    }

    @Override
    public boolean onTouchDown(float normalizedX, float normalizedY) {

        //if touch down not on the selected card , go back to  default state , else go to drag state
        YANVector2 touchToWorldPoint = YANInputManager.touchToWorld(normalizedX, normalizedY,
                EngineWrapper.getRenderer().getSurfaceSize().getX(), EngineWrapper.getRenderer().getSurfaceSize().getY());

        //find touched card under the touch point
        CardNode touchedCard = mCardsTouchProcessor.findTouchedCard(touchToWorldPoint);

        if (touchedCard == null)
            return false;

        mCandidateForTapCard = touchedCard;
        mLastTouchDownForTap = System.currentTimeMillis();
        return true;
    }

}